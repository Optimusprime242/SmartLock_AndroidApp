package com.example.hoangkhanh.smartlock;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class DevicePairedActivity extends ListActivity {

    private String deviceName;
    private String deviceHardwareAddress;
    ListView name;
    ArrayList<String> arrList = null;
    ArrayAdapter<String> adapter = null;

    ArrayList<BleDevices> bdv = new ArrayList<BleDevices>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_device_activity);

        name = (ListView) findViewById(android.R.id.list);
        name.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        //init listview for showing devices
        arrList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>
                (this,
                        android.R.layout.simple_list_item_multiple_choice,
                        arrList);
        name.setAdapter(adapter);

        //Show paired devices
        ShowPairedDevices();

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                BleDevices bledv = bdv.get(position);
                bledv.setActive(currentCheck);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        int i;
        for(i = 0; i < bdv.size(); i++)
        {
            if(bdv.get(i).isActive() == true)
                break;
        }
        name.setItemChecked(i, false);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void ShowPairedDevices() {
        BluetoothAdapter bleAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BleDevices a = new BleDevices(null, null);
                deviceName = device.getName();
                deviceHardwareAddress = device.getAddress(); // MAC address
                a.setName(deviceName);
                a.setAddress(deviceHardwareAddress);
                bdv.add(a);
                arrList.add("Name: " + deviceName + '\n' + "Address: " + deviceHardwareAddress);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void ConnectAction(View view) {
        int count = NumberOfChecked();
        if(count == 1) {
            for (int i = 0; i < bdv.size(); i++) {
                if (bdv.get(i).isActive() == true) {
                    final Intent intent = new Intent(getApplicationContext(), DeviceControlActivity.class);
                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, bdv.get(i).getName());
                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, bdv.get(i).getAddress());
                    startActivity(intent);
                }
            }
        }
        else if(count == 2)
            Toast.makeText(this, R.string.num_device_over, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.not_choose_yet, Toast.LENGTH_LONG).show();
    }

    public void RemovePair(View view) {
        BluetoothAdapter bleAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    for (int i = 0; i < bdv.size(); i++) {
                        if (bdv.get(i).isActive() == true) {
                            if (bdv.get(i).getAddress().equals(device.getAddress())) {
                                Method m = device.getClass()
                                        .getMethod("removeBond", (Class[]) null);
                                m.invoke(device, (Object[]) null);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        adapter.clear();
        adapter.notifyDataSetChanged();
        ShowPairedDevices();
    }

    public int NumberOfChecked()
    {
        int count = 0;
        for(int i = 0; i < bdv.size(); i++)
        {
            if(bdv.get(i).isActive() == true)
                count++;
        }
        return count;
    }
}
