package com.codeopticals.activitiestracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;


public class Register extends Activity {
    EditText Username ,ID;
    Button Submit;
    String Name="",Identity="";

    private String[] identifications= {
            "393557478", "393557469", "393557431", "392778729", "392520882",
            "392593793", "392747942", "392782072", "392520919", "393355467",
            "393557459", "393063207", "393462597", "393557552", "393557492",
            "392747953", "392520898", "393063230", "392848594", "392520878",
            "394930514", "393063250", "392593810", "393462609", "392593911",
            "393462599", "392593987", "392535023", "393063256", "392783601",
            "392775574", "393442551", "401737037", "393837738", "392593670",
            "393450057", "392520928", "392520953", "393449085", "393063213",
            "393063168", "392520921", "392535032", "393063237", "392559855",
            "393377116", "393381588", "392593896", "392520910", "392747933",
            "393063177", "393063235", "393462605", "393452659", "393240238",
            "393063218", "392593619", "393462592", "393063227", "393168961",
            "392894201", "393837732", "392593884", "393557439", "393450922",
            "392520887", "392747958", "392784363", "392594044", "393346334",
            "393253262", "401737544", "393063261", "393063241", "393837736",
            "393557508", "392593849", "393063232", "392535027", "392520863","393837732", "392777876", "392780765", "392780160", "392747961", "393063224", "393451195", "392520868", "393440385", "392535029",
            "399542915", "398951067", "398951067", "400028728", "398903293", "400141276", "398671300",
            "399166895", "399161293", "398683910", "398678109", "399845325", "399542886", "398650787",
            "400010559", "398951225", "400449331", "399542841", "398903308", "398903326", "400028734",
            "400449333", "400449337", "398951440", "398951074", "398951333", "398665724", "400028729",
            "399542873", "398903409", "398951296", "400059388", "398951479", "399550966", "398951321",
            "399154418", "398703973", "398649699", "398235126", "398235177", "398235051", "393381588",
            "393452659", "393450922","400028745",
            "400010561",
            "398903285",
            "398903445",
            "399010713"

    };
    private String[] userNames=
            {
            "Aba Yaa", "Abiiba Sulemana", "Agnes Agbahode", "Aisha Mumuni", "Akibatu Muntari",
            "Akos Tenu", "Alice Tsatsu", "Ama Nyarko", "Amina Musa", "Asyah Razak",
            "Ayishetu Mamudu", "Baratu Hakim", "Beatrice Boateng", "Beatrice Pokua", "Belinda Adjei",
            "Bintu Abdul Mumuni", "Bridget Amevor", "Casandra Agyiri", "Cynthia Okyere", "Deborah Dede Tetteh",
            "Diana Amatepey", "Diana Owusu", "Dzube Mabel", "Elizabeth Mills", "Ernestina Prah",
            "Estella Samfo", "Esther kpofianu", "Eunice Ayram", "Evelyn Larbi", "Faustina Otubea",
            "Florence Frimpong", "Gladys Armah", "Gladys Baafi", "Gladys Minta", "Gladys Osafo",
            "Grace Asieduaa", "Grace Awetey", "Grace Naah", "Grace Segbenu", "Hannah Anim",
            "Helen Ahiakpa", "Humidiatu Abdullah", "Joanne Cudjo", "Josephine Aidoo", "Joyce Kota",
            "Joyce Mensah", "Juliet Danso", "Justina Antwi", "Latifa Fuseni", "Lina Annan",
            "Mansard Sallah", "Martha Avornu", "Mary Adobea", "Mary Amoah", "Mary Boadi",
            "Mary Zigah", "Matilda Badu", "Mavis Adobea", "Mawuda Mumuni", "Milicent Ayorko Adjetey",
            "Monique Cobbold", "Mushira Osman", "Naomi Aboagye", "Patience Lettu-Boateng", "Ramatu Iddrisu",
            "Richlove Dwanene", "Roberta Deloris Amandey", "Rose Osei", "Rosemond Boateng", "Rosina Konadu",
            "Sadia Ishmael", "Saida Nassan", "Selina Whittle", "Shafiya Mukaila", "Sheila Yeboah",
            "Sophia Omoano", "Sophia Tamakloe", "Stella Egyiri", "Ziina Zakari", "Sahadatu Yussif","Mushira Osman", "Cynthia Quashie", "Georgina Sorsey", "Gladys Numiesi", "Hawa Mohammed", "Josephine Amedoame", "Mawuse Tagbor", "Mercy Achiaa", "Ruth Amankwa", "Yaa Nkansah",
                    "Agnes Owusu", "Aku Amenyo", "Angela Numanu", "Batrice laryea", "Belinda Ansah", "Comfort Animwaa", "Comfort Badu",
                    "Cynthia Benni", "Cynthia Boateng", "Cynthia Tetteh", "Deborah Asamani", "Dorcas Asare", "Edna Sackeyfio",
                    "Elizabeth Abroquah", "Elizabeth Akakpo", "Ernestina Boateng", "Esther Ansah", "Evelyn Amoah", "Fausia Aryee",
                    "Florence Abebrese", "Jenifer", "Leticia Odabah", "Linda Antwi", "Matilda Lartey", "Mercy Dickson", "Millicent Odoi",
                    "Patience Adjeley Tetteh", "Patience Ashiabi", "Perpetual Yankey", "Philomena Kowuvi", "Rita Ofusu Asare",
                    "Salomey Sackey Addo", "Sandra Ellis Essuman", "Shallot Semarco", "Sharon", "Victoria Sasraku", "Yaa Sarfoa",
                    "Eva Esi Maison", "Chrisriana Yeboah", "Lawrencia Osei Agyemang", "Rita Ansah", "Juliet Danso", "Mary Amoah",
                    "Ramatu Iddrisu",
                    "Frempomaa Nyarko",
                    "Hannah Lamptey",
                    "Ophelia Boahemaa",
                    "Gertrude Kwofie",
                    "Priscilla Nettey"


            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Username = (EditText)findViewById(R.id.name);

        Submit= (Button) findViewById(R.id.submit) ;

        Paper.init(this);

        Spinner ispinner= findViewById(R.id.spinner);

        String[] values = new String[identifications.length];

        for(int i =0; i< identifications.length; i++) values[i] = (identifications[i]+" - "+ userNames[i]);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,values);
        //adapter.setDropDownViewResource(R.style.SpinnerWithSpacing);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item); // Set the custom dropdown item layout
        ispinner.setAdapter(adapter);




        ispinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Identity = identifications[position];
                Name= userNames[position];
                Username.setText(Name);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });



        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setMessage("Are you sure you are "+Name)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Name.isEmpty()) {
                                    Toast.makeText(Register.this, "Supply Name", Toast.LENGTH_SHORT).show();
                                } else if (Identity.isEmpty()) {
                                    Toast.makeText(Register.this, "Supply Identity", Toast.LENGTH_SHORT).show();
                                } else {
                                    Paper.book().write("username", Name);
                                    Paper.book().write("identity", Identity);
                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();




            }
        });



    }
}