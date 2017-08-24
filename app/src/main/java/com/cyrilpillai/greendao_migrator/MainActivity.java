package com.cyrilpillai.greendao_migrator;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cyrilpillai.greendao_migrator.database.person.Person;
import com.cyrilpillai.greendao_migrator.database.person.PersonRepo;
import com.cyrilpillai.greendao_migrator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.rvPeoples.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPeoples.setHasFixedSize(true);
        personAdapter = new PersonAdapter();
        binding.rvPeoples.setAdapter(personAdapter);
        getListOfPeople();

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    private void getListOfPeople() {
        personAdapter.setData(PersonRepo.getInstance().listAllPeople());
    }

    private void addItem() {
        Person person = new Person();
        person.setName("Person " + personAdapter.getItemCount());
        person.setAge(personAdapter.getItemCount());
        PersonRepo.getInstance().savePerson(person);
        personAdapter.addItem(person);
    }
}
