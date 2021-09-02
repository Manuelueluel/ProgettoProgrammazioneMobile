package com.unitn.lpsmt.group13.pommidori.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import top.defaults.colorpicker.ColorPickerPopup;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
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
    private TextInputLayout editNome, editSigla, editNomeScad;

    AutoCompleteTextView dropdownAvviso;

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
        hourButton.setText(getNowHour());

        dateCheckBox = view.findViewById(R.id.checkbox_date);
        dateCheckBox.setChecked(false);
        dateLayout = view.findViewById(R.id.take_date);

        dropdownAvviso = view.findViewById(R.id.dropdown_avviso_new_activity);

        creaButton = view.findViewById(R.id.crea_activity);
        annullaButton = view.findViewById(R.id.annulla_activity);

        //Metodi
        initDatePicker();
        setButtonListeners();
        setEdidTextListener();
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
                if(!validateName() | !validateNameScadenza() | !validateSigla()){
                    return;
                }

                TableActivityModel m = new TableActivityModel();
                m.setName(editNome.getEditText().getText().toString().trim());
                m.setSigla(editSigla.getEditText().getText().toString().trim());
                m.setNomeScadenza(editNomeScad.getEditText().getText().toString().trim());
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
        });
        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNome.getEditText().setText("");
                editNomeScad.getEditText().setText("");
                editSigla.getEditText().setText("");

                getActivity().finish();
            }
        });
    }

    private void setEdidTextListener(){
        editNome.getEditText().addTextChangedListener(editText);
        editNomeScad.getEditText().addTextChangedListener(editText);
        editSigla.getEditText().addTextChangedListener(editText);
    }

    private TextWatcher editText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = editNome.getEditText().getText().toString().trim();
            String nameScad = editNomeScad.getEditText().getText().toString().trim();
            String sigla = editSigla.getEditText().getText().toString().trim();

            if(!name.isEmpty())
                editNome.setError(null);
            if(!nameScad.isEmpty())
                editNomeScad.setError(null);
            if(!sigla.isEmpty())
                editSigla.setError(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        return "--/--/--"; //non visualizzo la data così da far capire che bisogna selezionarla
    }
    private String getNowHour(){
        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);

        return "--:--"; //non visualizzo la data così da far capire che bisogna selezionarla
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

    private boolean validateName(){
        String name = editNome.getEditText().getText().toString().trim();

        if(name.isEmpty()){
            editNome.setError("Campo obbligatorio");
            return  false;
        }else{
            editNome.setError(null);
            return true;
        }
    }
    private boolean validateNameScadenza(){
        String nameScad = editNomeScad.getEditText().getText().toString().trim();

        if(nameScad.isEmpty()){
            editNomeScad.setError("Campo obbligatorio");
            return  false;
        }else{
            editNomeScad.setError(null);
            return true;
        }
    }
    private boolean validateSigla(){
        String sigla = editSigla.getEditText().getText().toString().trim();

        if(sigla.isEmpty()){
            editSigla.setError("Campo obbligatorio");
            return  false;
        }else if(sigla.length()<3 || sigla.length()>3){
            editSigla.setError("3 caratteri");
            return false;
        }else{
            editSigla.setError(null);
            return true;
        }
    }
}