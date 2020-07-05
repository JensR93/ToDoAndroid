package com.jens.ToDo.ui.DetailView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

    private Activity Activity;
    private ToDo selectedToDoItem;
    private List<View> itemViewList;
    private ContactManager contactmanager;
    private ListView listView;


    public ContactListItem(Activity Activity, ToDo selectedItem, ContactManager contactmanager, ListView listView) {
        try {
            this.Activity = Activity;
            this.selectedToDoItem =selectedItem;
            itemViewList=new ArrayList<>();
            this.contactmanager=contactmanager;

            this.listView=listView;
            createContactList();
        }
        catch (Exception e){
            int a=0;
        }
    }


    /**
     * Creates the listViewAdapter
     * Add the contactList from the ToDo-Element to the listViewAdapter
     * Set the listViewAdapter to the listView
     */
    private void createContactList() {
        ArrayAdapter<ToDoContact> listViewAdapter = createListViewAdapter(selectedToDoItem.getToDoContactList());
        listViewAdapter.addAll(selectedToDoItem.getToDoContactList());
        listView.setAdapter(listViewAdapter);
    }

    /**
     * Create the ListviewAdapter that will be set on the Listview
     * The View contains the listener for the showContact-button, the email and sms button
     * The buttons always use the first adress / first number for the intent
     * Buttons will be disabled if there is no number / adress for the contact
     * @param selectedListItem
     * @return
     */
    private ArrayAdapter<ToDoContact> createListViewAdapter(List<ToDoContact> selectedListItem) {
        return new ArrayAdapter<ToDoContact>(Activity, R.layout.activity_detail_contact_listitem) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {

                TextView itemNameView;
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

                itemView.setOnClickListener(v ->showContactPopup(selectedListItem.get(position)));

                itemNameView.setOnClickListener(v -> showContactPopup(selectedListItem.get(position)));

                buttonEmail.setOnClickListener(v -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"+selectedListItem.get(position).getEmailAdress()[0]));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, selectedToDoItem.getName());
                    emailIntent.putExtra(Intent.EXTRA_TEXT, selectedToDoItem.getDescription());
                    Activity.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
                });
                buttonSMS.setOnClickListener(v -> {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    smsIntent.setData(Uri.parse("smsto:" + selectedListItem.get(position).getPhoneNo()[0])); // This ensures only SMS apps respond
                    smsIntent.putExtra("sms_body", selectedToDoItem.getDescription());
                    Activity.startActivity(smsIntent);
                });
                buttonDelete.setOnClickListener(v -> {
                    ToDoContact toDoContact = selectedListItem.get(position);
                    if(selectedListItem!=null||selectedToDoItem!=null){
                    selectedToDoItem.removeToDoContact(toDoContact);
                    createContactList();
                    removeViewFromList(v);
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

    /**
     * Removes the selected contact from the itemViewList
     * @param clieckedViewItem
     */
    private void removeViewFromList(View clieckedViewItem) {
        boolean check = false;
        for (View view : itemViewList)if(!check&&clieckedViewItem==view)check=true;
        if(check)itemViewList.remove(clieckedViewItem);
    }


    /**
     * Show a seperat Popup for the contact
     * The popup contains all phone numbers and emails
     * Buttons for call, message for each number
     * Buttons for email to each adress
     * @param toDoContact
     */
    private void showContactPopup(ToDoContact toDoContact) {
        Dialog myDialog=new Dialog(Activity);
        myDialog.setContentView(R.layout.activity_detail_contacts_popup);
        TextView contactName = myDialog.findViewById(R.id.contactName);
        ListView listViewEmail = myDialog.findViewById(R.id.listviewEmail);
        ListView listViewTel = myDialog.findViewById(R.id.listviewTel);

        ArrayAdapter<String> arrayAdapterTel = new ArrayAdapter<String>(Activity,R.layout.activity_detail_telefon){
            @NonNull
            @Override
            public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
                itemView = Activity.getLayoutInflater().inflate(R.layout.activity_detail_telefon, null);
                TextView tel = itemView.findViewById(R.id.textTelefon);
                ImageButton imageButtonSMS= itemView.findViewById(R.id.imageButtonSMS);
                ImageButton imageButtonCall= itemView.findViewById(R.id.imageButtonCall);
                String number = toDoContact.getPhoneNo()[position];
                tel.setText(number);

                imageButtonSMS.setOnClickListener(v -> contactmanager.sendSMS(toDoContact.getPhoneNo()[position],"Hello"));
                imageButtonCall.setOnClickListener(v -> contactmanager.startCall(toDoContact.getPhoneNo()[position]));
                return itemView;
            }

        };
        ArrayAdapter<String> arrayAdapterEmail = new ArrayAdapter<String>(Activity,R.layout.activity_detail_email){
            @NonNull
            @Override
            public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
                itemView = Activity.getLayoutInflater().inflate(R.layout.activity_detail_email, null);
                TextView email = itemView.findViewById(R.id.textEmail);
                ImageButton imageButtonEMail= itemView.findViewById(R.id.imageButtonEmail);
                String adress = toDoContact.getEmailAdress()[position];
                email.setText(adress);
                imageButtonEMail.setOnClickListener(v -> contactmanager.sendEmail(toDoContact.getEmailAdress()[position],"Hello","TestEmail"));

                return itemView;
            }

        };
        arrayAdapterTel.addAll(toDoContact.getPhoneNo());
        arrayAdapterEmail.addAll(toDoContact.getEmailAdress());
        listViewTel.setAdapter(arrayAdapterTel);
        listViewEmail.setAdapter(arrayAdapterEmail);
        contactName.setText(toDoContact.getName());
        myDialog.show();
    }



}
