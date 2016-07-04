package com.example.utar.bluetoothchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by Yumiko on 6/17/2016.
 */

public class TabConnectionFragment extends Fragment {

    View v;

    ListView listDevicesFound;
    ListView pairDevicesFound;
    Button btnScanDevice;
    BluetoothAdapter bluetoothAdapter;

    ArrayAdapter<String> deviceAdapter;
    ArrayList<String> deviceList;
    ArrayAdapter<String> pairAdapter;
    ArrayList<String> pairList;

    BluetoothDevice device;

    private static final UUID aUUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothChatService mChatService = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = getView();

        return v = inflater.inflate(R.layout.tab_connection_fragment, container, false);
    }

    /** Called when the activity is first created. */
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnScanDevice = (Button)v.findViewById(R.id.scanbtn);
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        listDevicesFound = (ListView)v.findViewById(R.id.devicelist);
        listDevicesFound.setOnItemClickListener(new onPairListItemClickListener());

        deviceList = new ArrayList<String>();
        deviceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, deviceList);
        listDevicesFound.setAdapter(deviceAdapter);

        pairDevicesFound = (ListView)v.findViewById(R.id.pairlist);
        pairDevicesFound.setOnItemClickListener(new onConnectListItemClickListener());

        showPairDevice();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        getActivity().unregisterReceiver(ActionFoundReceiver);
        getActivity().unregisterReceiver(mPairReceiver);

    }

    //When scan button is click
    private Button.OnClickListener btnScanDeviceOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            deviceAdapter.clear();

            bluetoothAdapter.startDiscovery();

            Toast.makeText(getActivity(), "Scanning in process. Please wait.",
                    Toast.LENGTH_LONG).show();

            getActivity().registerReceiver(ActionFoundReceiver,
                    new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }};

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            // When discovery finds a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    deviceList.add(device.getName() + "\n" + device.getBondState() + "\n" + device.getAddress());
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        }};


    private class onPairListItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            if (device.getBondState() == 10) {
                //Pair request dialog pops out
                AlertDialog.Builder pairBuilder = new AlertDialog.Builder(getContext());

                pairBuilder.setMessage("Do you want to pair with" + device.getName() + "?");
                pairBuilder.setCancelable(true);

                //Pairing action is perform
                pairBuilder.setPositiveButton(
                        "Pair",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                pairDevice(device);

                                IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                                getActivity().registerReceiver(mPairReceiver, intent);
                            }
                        });

                pairBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog pairAlert = pairBuilder.create();
                pairAlert.show();

            } else if(device.getBondState() == 12) {
                Toast.makeText(getActivity(),"Already paired", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPairDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        pairList = new ArrayList<String>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                pairList.add(device.getName() + "\n" + device.getBondState() + "\n" + device.getAddress());
                ArrayAdapter pairAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, pairList);
                pairDevicesFound.setAdapter(pairAdapter);
                pairAdapter.notifyDataSetChanged();
            }
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(getActivity(),"Paired", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    private class onConnectListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            final BluetoothDevice theDevice = device;

            if (device.getBondState() == 12 && theDevice != null) {
                //Connect request dialog pops out
                AlertDialog.Builder connectBuilder = new AlertDialog.Builder(getContext());

                connectBuilder.setMessage("Do you want to connect with " + device.getName() + "?");
                connectBuilder.setCancelable(true);

                //Connect action is perform
                connectBuilder = connectBuilder.setPositiveButton(
                        "Connect",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                bluetoothAdapter.cancelDiscovery();

                                // Get the device MAC address, which is the last 17 chars in the View
                                String info = ((TextView) v).getText().toString();
                                String address = info.substring(info.length() - 17);

                                // Create the result Intent and include the MAC address
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                                mChatService.connect(device);

                            }
                        });

                connectBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog connectAlert = connectBuilder.create();
                connectAlert.show();
            }
        }
    }

}

