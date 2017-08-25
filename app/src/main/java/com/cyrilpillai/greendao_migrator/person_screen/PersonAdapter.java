package com.cyrilpillai.greendao_migrator.person_screen;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cyrilpillai.greendao_migrator.R;
import com.cyrilpillai.greendao_migrator.database.person.Person;
import com.cyrilpillai.greendao_migrator.databinding.PersonItemBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private List<Person> data;
    private Context context;

    public PersonAdapter(Context context) {
        data = new ArrayList<>();
        this.context = context;
    }

    public void setData(List<Person> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addItem(Person person) {
        int position = getItemCount();
        data.add(position, person);
        notifyItemInserted(position);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PersonItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.person_item, parent, false);
        return new PersonViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        Person person = data.get(position);
        holder.binding.tvName.setText(person.getName());
        holder.binding.tvAge.setText(String.format(Locale.getDefault(),
                context.getString(R.string.age_placeholder), person.getAge()));
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        private PersonItemBinding binding;

        PersonViewHolder(PersonItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
