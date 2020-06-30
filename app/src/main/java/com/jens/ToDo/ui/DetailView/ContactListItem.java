package com.jens.ToDo.ui.DetailView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoContact;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.List;

public class ContactListItem {


    private TextView itemNameView;
    private TextView itemNameView2;
    private ArrayAdapter<ToDoContact> listViewAdapter;
    private ArrayAdapter<ToDoContact> listViewAdapter2;

    private ListView listView;
    private ListView listView2;
    private Activity Activity2;
    private Activity Activity;
    private ToDo selectedItem;
    private ToDo selectedItem2;

    public ContactListItem(DetailViewActivity Activity, ToDo selectedItem) {
        try {
            this.Activity = Activity;
            this.selectedItem=selectedItem;
            this.listView = Activity.findViewById(R.id.listView2);

            listViewAdapter = createListViewAdapter(selectedItem.getToDoContactList());
            listViewAdapter.addAll(selectedItem.getToDoContactList());
            listView.setAdapter(listViewAdapter);
        }
        catch (Exception e){
            int a=0;
        }
    }
    public ContactListItem(MainActivity Activity, ToDo selectedItem){
        this.Activity = Activity;
        this.selectedItem=selectedItem;
        listViewAdapter = createListViewAdapter(selectedItem.getToDoContactList());
    }
    public ContactListItem(MainActivity Activity,ToDo selectedItem, View view) {
        try {
            this.Activity = Activity;
            this.selectedItem=selectedItem;
            this.listView = Activity.findViewById(R.id.listView2);

            listViewAdapter = createListViewAdapter(selectedItem.getToDoContactList());
            listViewAdapter.addAll(selectedItem.getToDoContactList());
            listView.setAdapter(listViewAdapter);
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

                itemNameView = itemView.findViewById(R.id.itemConactName);
                AppCompatImageButton buttonEmail = itemView.findViewById(R.id.imageButtonEMail);
                AppCompatImageButton buttonSMS = itemView.findViewById(R.id.imageButtonSMS);
                AppCompatImageButton buttonDelete = itemView.findViewById(R.id.imageButtonDelete);

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
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, selectedItem.getName());
                        emailIntent.putExtra(Intent.EXTRA_TEXT, selectedItem.getDescription());
                        Activity.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
                    }
                });
                buttonSMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        smsIntent.setData(Uri.parse("smsto:" + selectedListItem.get(position).getPhoneNo())); // This ensures only SMS apps respond
                        smsIntent.putExtra("sms_body", selectedItem.getDescription());
                        Activity.startActivity(smsIntent);
                    }
                });
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedItem.removeToDoContact(selectedListItem.get(position));
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


}
