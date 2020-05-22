package com.jens.ToDo.ui.DetailView;

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

import java.util.List;

public class ContactListItem {


    private TextView itemNameView;

    private ArrayAdapter<ToDoContact> listViewAdapter;

    private ListView listView;
    private DetailViewActivity detailViewActivity;
    private ToDo selectedItem;
    public ContactListItem(DetailViewActivity detailViewActivity, ToDo selectedItem) {
        try {
            this.selectedItem=selectedItem;
            this.listView = detailViewActivity.findViewById(R.id.listView2);
            this.detailViewActivity = detailViewActivity;
            listViewAdapter = createListViewAdapter(selectedItem.getToDoContactList());
            listViewAdapter.addAll(selectedItem.getToDoContactList());
            listView.setAdapter(listViewAdapter);
        }
        catch (Exception e){
            int a=0;
        }
    }

    private ArrayAdapter<ToDoContact> createListViewAdapter(List<ToDoContact> selectedListItem) {
        return new ArrayAdapter<ToDoContact>(detailViewActivity, R.layout.activity_main_listitem, R.id.itemName) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = convertView;

                itemView = detailViewActivity.getLayoutInflater().inflate(R.layout.activity_detail_contact_listitem, null);

                itemNameView = itemView.findViewById(R.id.itemConactName);
                AppCompatImageButton buttonEmail = itemView.findViewById(R.id.imageButtonEMail);
                AppCompatImageButton buttonSMS = itemView.findViewById(R.id.imageButtonSMS);
                AppCompatImageButton buttonDelete = itemView.findViewById(R.id.imageButtonDelete);

                buttonEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:"+selectedListItem.get(position).getEmailAdress()));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, selectedItem.getName());
                        emailIntent.putExtra(Intent.EXTRA_TEXT, selectedItem.getDescription());
                        detailViewActivity.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
                    }
                });
                buttonSMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        smsIntent.setData(Uri.parse("smsto:" + selectedListItem.get(position).getPhoneNo())); // This ensures only SMS apps respond
                        smsIntent.putExtra("sms_body", selectedItem.getDescription());
                        detailViewActivity.startActivity(smsIntent);
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
