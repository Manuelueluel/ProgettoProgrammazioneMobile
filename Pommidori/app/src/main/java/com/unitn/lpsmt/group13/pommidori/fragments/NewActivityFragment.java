package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import top.defaults.colorpicker.ColorPickerPopup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.unitn.lpsmt.group13.pommidori.Calendario;
import com.unitn.lpsmt.group13.pommidori.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewActivityFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Button dateButton,hourButton, creaButton, annullaButton;
    private CheckBox dateCheckBox;
    private EditText editNome, editSigla, editNomeScad;

    AutoCompleteTextView dropdownAvviso;

    //Validazione dei editext
    AwesomeValidation awesomeValidation;

    private View view, colorPicker;
    private LinearLayout dateLayout;

    //Dati
    int year, month, day, hour, minute;
    int color;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_activity, container, false);



        colorPicker = view.findViewById(R.id.color_piclker);
        ColorDrawable c = (ColorDrawable) colorPicker.getBackground();
        color = c.getColor();

        editNome = view.findViewById(R.id.nome_attivita);
        editSigla = view.findViewById(R.id.sigla_attivita);
        editNomeScad = view.findViewById(R.id.nome_scadenza);


        dateButton = view.findViewById(R.id.date_picker);
        dateButton.setText(getTodayDate());
        hourButton = view.findViewById(R.id.hour_picker);
        Date d = new Date();
        SimpleDateFormat dd = new SimpleDateFormat("HH:mm", Locale.ITALY);
        hourButton.setText(dd.format(d));

        dateCheckBox = view.findViewById(R.id.checkbox_date);
        dateCheckBox.setChecked(false);
        dateLayout = view.findViewById(R.id.take_date);

        dropdownAvviso = view.findViewById(R.id.dropdown_avviso_new_activity);

        creaButton = view.findViewById(R.id.crea_activity);
        annullaButton = view.findViewById(R.id.annulla_activity);

        //Metodi
        initDatePicker();
        setButtonListeners();
        setAwesomeValidation();
        setDropDownLists();

        return view;
    }
    private void setButtonListeners(){
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerPopup.Builder(getContext()).okTitle("Ok").cancelTitle("Annulla")
                        .enableAlpha(false).enableBrightness(false).showIndicator(true)
                        .build().show(view,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int c) {
                                color = c;
                                colorPicker.setBackgroundColor(color);
                            }
                        });
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        hourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePiker();
            }
        });
        dateCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Disattiva/Riattiva selezione ore e minuti
                setViewAndChildrenEnabled(dateLayout, !dateCheckBox.isChecked());
            }
        });
        creaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNome.getText().length() < 1 || editSigla.getText().length() < 3 || (editNomeScad.getText().length() < 1 && !dateCheckBox.isChecked())) {
                    Toast.makeText(getContext(), "Errore: Compilare tutti i campi richiesti", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (awesomeValidation.validate()) {
                    TableActivityModel m = new TableActivityModel();
                    m.setName(editNome.getText().toString());
                    m.setSigla(editSigla.getText().toString());
                    m.setNomeScadenza(editNomeScad.getText().toString());
                    m.setColore(color);

                    if (dateCheckBox.isChecked()) {   //una attività "infinita" ha la data settata all'anno 0 (1/1/1970)
                        m.setScadenza(new Date(0));
                    } else {
                        try {
                            String d = year + "-" + month + "-" + day + " " + hour + ":" + minute;
                            m.setScadenza(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ITALY).parse(d));
                        } catch (Exception e){
                            return;
                        }
                    }

                    Database db = new Database(getContext());
                    if (db.addActivity(m)) {
                        Toast.makeText(getContext(), "Attività creata!", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Errore creazione attività", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNome.setText("");
                editNomeScad.setText("");
                editSigla.setText("");

                getActivity().finish();
            }
        });
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        return day+"/"+month+"/"+year;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                year = _year;
                month = _month+1;
                day = _day;
                //testo bottone datePicker
                dateButton.setText(day+"/"+month+"/"+year);
            }
        };

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this.getContext(),dateSetListener,year,month,day);
    }

    private void popTimePiker(){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                String time = hour+":"+(minute<10?"0"+minute:minute);
                //testo bottone timePicker
                hourButton.setText(time);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Scegli l'ora");
        timePickerDialog.show();
    }

    //Validazione dei campi form
    private void setAwesomeValidation(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //validation Nome
        awesomeValidation.addValidation(getActivity(),R.id.nome_attivita
                ,RegexTemplate.NOT_EMPTY,R.string.nome_attivita);
        //validation Sigla
        awesomeValidation.addValidation(getActivity(),R.id.sigla_attivita
                ,".{3}",R.string.sigla);

        if(!dateCheckBox.isChecked()){
            //validation Nome Scadenza
            awesomeValidation.addValidation(getActivity(),R.id.nome_scadenza
                    ,RegexTemplate.NOT_EMPTY,R.string.nome_scadenza);
        }
    }

    //Attiva o disattiva (enabled) tutte le Views che sono figlie di view, essa stessa compresa
    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }
    private void setDropDownLists(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.avviso,
                R.layout.dropdown_item
        );

        dropdownAvviso.setText(adapter.getItem(0));

        dropdownAvviso.setAdapter(adapter);
    }
}