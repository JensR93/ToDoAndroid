package com.jens.ToDo.ui.DetailView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoContact;

import java.util.ArrayList;
import java.util.List;

public class ContactListItem {


    private TextView itemNameView;
    private ArrayAdapter<ToDoContact> listViewAdapter;

    private ListView listView;
    private DetailViewActivity Activity;
    private ToDo selectedToDoItem;
    private List<View> itemViewList;


    public ContactListItem(DetailViewActivity Activity, ToDo selectedItem) {
        try {
            this.Activity = Activity;
            this.selectedToDoItem =selectedItem;
            this.listView = Activity.findViewById(R.id.listView2);
            itemViewList=new ArrayList<>();
            //listViewAdapter = createListViewAdapter(selectedItem.getToDoContactList());
            //listViewAdapter.addAll(selectedItem.getToDoContactList());
            //listView.setAdapter(listViewAdapter);
        }
        catch (Exception e){
            int a=0;
        }
    }



    public ArrayAdapter<ToDoContact> createListViewAdapter(List<ToDoContact> selectedListItem) {
        return new ArrayAdapter<ToDoContact>(Activity, R.layout.activity_detail_contact_listitem) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = convertView;

                itemView = Activity.getLayoutInflater().inflate(R.layout.activity_detail_contact_listitem, null);
                itemViewList.add(itemView);
                itemNameView = itemView.findViewById(R.id.itemConactName);
                AppCompatImageButton buttonEmail = itemView.findViewById(R.id.imageButtonEMail);
                AppCompatImageButton buttonSMS = itemView.findViewById(R.id.imageButtonSMS);
                AppCompatImageButton buttonDelete = itemView.findViewById(R.id.imageButtonDelete);
                ImageView imageView = itemView.findViewById(R.id.itemContactPhoto);
                if(selectedListItem.get(position).getPhoto()!=null){
                    imageView.setImageBitmap(selectedListItem.get(position).getPhoto());
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int a=0;
                        View parent = (View) v.getParent();

                    }
                });

                buttonEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:"+selectedListItem.get(position).getEmailAdress()));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, selectedToDoItem.getName());
                        emailIntent.putExtra(Intent.EXTRA_TEXT, selectedToDoItem.getDescription());
                        Activity.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
                    }
                });
                buttonSMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        smsIntent.setData(Uri.parse("smsto:" + selectedListItem.get(position).getPhoneNo())); // This ensures only SMS apps respond
                        smsIntent.putExtra("sms_body", selectedToDoItem.getDescription());
                        Activity.startActivity(smsIntent);
                    }
                });
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoContact toDoContact = selectedListItem.get(position);
                        if(selectedListItem!=null||selectedToDoItem!=null){
                        selectedToDoItem.removeToDoContact(toDoContact);
                        Activity.createContactList();
                        removeViewFromList(v);
                        }
                    }
                });


                if (itemNameView != null) {
                    itemNameView.setText(selectedListItem.get(position).getName());

                    if(selectedListItem.get(position).getEmailAdress()==null){
                        buttonEmail.setEnabled(false);
                    }

                }
                return itemView;
            }
        };

    }

    private void removeViewFromList(View v) {
        boolean check = false;
        for (View view : itemViewList){
            if(!check&&v==view){
                check=true;
            }
        }
        if(check){
            itemViewList.remove(v);

        }
    }


}
