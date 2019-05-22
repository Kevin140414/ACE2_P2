package com.juandelcid.ace2fp;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class AutomaticShower extends AppCompatActivity {
    //count down
    private static final long START_TIME_IN_MILLIS = 600000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    // to reproduce music
    int posicion = 0;
    MediaPlayer vectormp [] = new MediaPlayer[3];

    TextView tvInfMode;
    TextView tvInfConex;
    TextView tvUsedWater;
    TextView tvCountDown;
    TextView textView30;

    ImageButton btnRock;
    ImageButton btnClasic;
    ImageButton btnElectric;
    ImageButton btnStart;

    TextView tvDato;
    TextView tvInfo;



    boolean  FLAG = false;

    String NOMBRE_HORARIO;
    int SHOWDATE = 0;
    int SHOWHOUR = 0;
    int SHOWTEMPE = 0;
    int INTERVALODHT = 0;

    private ArrayAdapter horariosArrayAdapter;
    private ArrayAdapter mensajesArrayAdapter;

    int SEGUNDO = 1000;
    String RESULTADO_ARDUINO = "";


    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    // Debugging
    public static final String TAG = "ACE2";
    public static final boolean D = true;
    // Tipos de mensaje enviados y recibidos desde el Handler de ConexionBT
    public static final int Mensaje_Estado_Cambiado = 1;
    public static final int Mensaje_Leido = 2;
    public static final int Mensaje_Escrito = 3;
    public static final int Mensaje_Nombre_Dispositivo = 4;
    public static final int Mensaje_TOAST = 5;
    public static final int MESSAGE_Desconectado = 6;
    public static final int REQUEST_ENABLE_BT = 7;
    public static final int REQUEST_ADDRESS = 8;

    public static final String DEVICE_NAME = "device_name";
    public static String ADDRESS_DEVICE = "";
    public static final String TOAST = "toast";
    //Nombre del dispositivo conectado
    private String mConnectedDeviceName = null;
    // Adaptador local Bluetooth
    private BluetoothAdapter AdaptadorBT = null;
    //Objeto miembro para el servicio de ConexionBT
    private ConexionBT Servicio_BT = null;
    //Vibrador
    private Vibrator vibrador;
    //variables para el Menu de conexi�n
    private boolean seleccionador = false;
    public int Opcion = R.menu.activity_main;
    public int AVANCE_BARRA = 0;
    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_shower);
        inicializar();
    }

    private void inicializar() {

        tvDato = (TextView) findViewById(R.id.tvDato);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfConex = (TextView) findViewById(R.id.tvInfConex);
        tvInfMode = (TextView) findViewById(R.id.tvInfMode);
        textView30 = (TextView) findViewById(R.id.textView30);

        tvUsedWater = (TextView) findViewById(R.id.tvUsedWater);
        tvCountDown = (TextView) findViewById(R.id.tvCountDown);


        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInfo.setText("Value sent = M");
                sendMessage("A");
                PlayPause();
            }
        });
        vectormp[0] = MediaPlayer.create(this, R.raw.sound_rock);
        vectormp[1] = MediaPlayer.create(this, R.raw.sound_clasic);
        vectormp[2] = MediaPlayer.create(this, R.raw.sound_elect);

        btnRock = (ImageButton) findViewById(R.id.btnRock);
        btnRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FLAG){
                    Stop();
                    pauseTimer();
                    resetTimer();
                }
                posicion = 0;
                Toast.makeText( getApplicationContext(), "Rock.", Toast.LENGTH_LONG).show();
            }
        });
        btnClasic = (ImageButton) findViewById(R.id.btnClasic);
        btnClasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FLAG){
                    Stop();
                    pauseTimer();
                    resetTimer();
                }
                posicion = 1;
                Toast.makeText( getApplicationContext(), "Clasica.", Toast.LENGTH_LONG).show();
            }
        });
        btnElectric = (ImageButton) findViewById(R.id.btnElectric);
        btnElectric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FLAG){
                    Stop();
                    pauseTimer();
                    resetTimer();
                }
                posicion = 2;
                Toast.makeText( getApplicationContext(), "Electronica.", Toast.LENGTH_LONG).show();
            }
        });
        updateCountDownText();
    }
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                postValores();
                mTimerRunning = false;
                //btnStart.setText("Start");
                textView30.setText( "Start");
            }
        }.start();

        mTimerRunning = true;
        textView30.setText("pause");
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        textView30.setText("Start");
    }

    private void resetTimer() {
        postValores();
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void postValores(){
        int tiempoEfectivo = (int) (START_TIME_IN_MILLIS - mTimeLeftInMillis) / 1000;
        int tiempototal = tiempoEfectivo;
        double litros = tiempototal * 0.05;

        URLConnect2.ruta = "registro";

        URLConnect2.postDataParams = new JSONObject();
        try {
            URLConnect2.postDataParams.put("idusuario",  String.valueOf(VentanaPrincipal.id_user));
            URLConnect2.postDataParams.put("litros",  String.valueOf(litros));
            URLConnect2.postDataParams.put("tiempoefectivo",  String.valueOf(tiempoEfectivo));
            URLConnect2.postDataParams.put("tiempototal",  String.valueOf(tiempototal));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new URLConnect2.SendPostRequest().execute();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvCountDown.setText(timeLeftFormatted);
    }
    //Método para el botón PlayPause
    public void PlayPause(){
        if(vectormp[posicion].isPlaying()){
            postValores();
            vectormp[posicion].pause();
            btnStart.setBackgroundResource(R.drawable.reproducir);
            Toast.makeText(this, "Finalizado", Toast.LENGTH_SHORT).show();
            if (mTimerRunning) {
                pauseTimer();
                resetTimer();
            }
        } else {
            vectormp[posicion].start();
            btnStart.setBackgroundResource(R.drawable.pausa);
            Toast.makeText(this, "Taking a shower...", Toast.LENGTH_SHORT).show();
            FLAG = true;
            if (!mTimerRunning) {
                startTimer();
            }
        }
    }
    //Método para el botón Stop
    public void Stop(){
        if(vectormp[posicion] != null){
            vectormp[posicion].stop();

            vectormp[0] = MediaPlayer.create(this, R.raw.sound_rock);
            vectormp[1] = MediaPlayer.create(this, R.raw.sound_clasic);
            vectormp[2] = MediaPlayer.create(this, R.raw.sound_elect);

            posicion = 0;
            btnStart.setBackgroundResource(R.drawable.reproducir);
            //Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        }
    }
    private void confirmMode() {
        AlertDialog dialog = crearDialogoConfirmacion(getString(R.string.msg_SendData), "Are you sure?.",3);
        dialog.show();
    }

    private AlertDialog crearDialogoConfirmacion(String titulo, String mensaje, final int opcion) {
        // Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        // Creamos un nuevo OnClickListener para el boton OK que realice la conexion
        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** HACER LLAMDO DE LA FUNCION CLASE, ASYNCTASK**/
                if ( opcion == 0 ){
                    //send signal manual
                    tvInfo.setText("Manual mode.");
                    sendMessage("1");
                }
                else if ( opcion == 1 ){
                    //send signal automatic
                    tvInfo.setText("Automatic mode.");
                    sendMessage("2");
                }
                else if ( opcion == 3 ){
                    tvInfo.setText("Saver mode.");
                    sendMessage("3");
                }

            }
        };

        // Creamos un nuevo OnClickListener para el boton Cancelar
        DialogInterface.OnClickListener listenerCancelar = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        };
        // Asignamos los botones positivo y negativo a sus respectivos listeners
        alertDialogBuilder.setPositiveButton(R.string.msg_Ok, listenerOk);
        alertDialogBuilder.setNegativeButton(R.string.msg_Cancel, listenerCancelar);

        return alertDialogBuilder.create();
    }

    public void onStart() { //SE EJECUTA AL INICIAR EL ACTIVITY
        super.onStart();
        ConfigBT();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Servicio_BT != null) Servicio_BT.stop();//Detenemos servicio
    }

    public void ConfigBT() {
        // Obtenemos el adaptador de bluetooth
        AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
        if (AdaptadorBT.isEnabled()) {//Si el BT esta encendido,
            if (Servicio_BT == null) {//y el Servicio_BT es nulo, invocamos el Servicio_BT
                Servicio_BT = new ConexionBT(this, mHandler);
            }
        } else {
            if (D) Log.e("Setup", "Bluetooth apagado...");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Una vez que se ha realizado una actividad regresa un "resultado"...
        switch (requestCode) {

            case REQUEST_ENABLE_BT://Respuesta de intento de encendido de BT
                if (resultCode == Activity.RESULT_OK) {//BT esta activado,iniciamos servicio
                    ConfigBT();
                } else {//No se activo BT, salimos de la app
                    finish();
                }
                break;
            case REQUEST_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {//BT esta activado,iniciamos servicio
                    if (Servicio_BT != null) Servicio_BT.stop();//Detenemos servicio
                    ADDRESS_DEVICE = data.getExtras().getString(DeviceBT.EXTRA_DEVICE_ADDRESS);
                    //establecerConexion();
                } else {//No se activo BT, salimos de la app
                    //finish();
                }
                break;


        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menux) {
        //cada vez que se presiona la tecla menu  este metodo es llamado
        menux.clear();//limpiamos menu actual
        if (seleccionador == false) Opcion = R.menu.conecta;//dependiendo las necesidades
        if (seleccionador == true) Opcion = R.menu.desconecta;  // crearemos un menu diferente
        getMenuInflater().inflate(Opcion, menux);
        return super.onPrepareOptionsMenu(menux);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Conexion:
                if (D) Log.e("conexion", "conectandonos");
                //establecerConexion();
                vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrador.vibrate(100);
                if (ADDRESS_DEVICE.isEmpty()) {
                    //ADDRESS_DEVICE = "A0:02:DC:B3:11:5B"; //MAC FIRE PHONE
                    //ADDRESS_DEVICE = "00:03:AB:0B:F1:F2"; //MAC AX700 "00:03:AB:0B:F1:F2"
                    //ADDRESS_DEVICE = "98:D3:31:FD:13:70"; // MAC MODBLUE "" // <<<<<--------
                    ADDRESS_DEVICE = "98:D3:32:31:1C:85"; // MAC MODBLUE hc-05 "" // <<<<<--------
                }
                BluetoothDevice device = AdaptadorBT.getRemoteDevice(ADDRESS_DEVICE);
                Servicio_BT.connect(device);
                return true;

            case R.id.desconexion:
                if (Servicio_BT != null) Servicio_BT.stop();//Detenemos servicio
                return true;
            case R.id.m_scan:
                Intent serverIntent = new Intent(getBaseContext(), DeviceBT.class);
                startActivityForResult(serverIntent, REQUEST_ADDRESS);
                return true;
        }//fin de swtich de opciones
        return false;
    }//fin de metodo onOptionsItemSelected

    public void sendMessage(String message) {
        if (Servicio_BT.getState() == ConexionBT.STATE_CONNECTED) {//checa si estamos conectados a BT
            if (message.length() > 0) {   // checa si hay algo que enviar
                byte[] send = message.getBytes();//Obtenemos bytes del mensaje
                if (D) Log.e(TAG, "Sent Messsage:" + message);
                Servicio_BT.write(send);     //Mandamos a escribir el mensaje
                //tvInfo.setText("Se envio: " + message);
            }
        } else Toast.makeText(this, "No conectado", Toast.LENGTH_SHORT).show();


    }

    String penultimoMsg = "";
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                case Mensaje_Escrito:
                    byte[] writeBuf = (byte[]) msg.obj;//buffer de escritura...
                    // Construye un String del Buffer
                    String writeMessage = new String(writeBuf);
                    //if (D) Log.e(TAG, "Message_write  =w= " + writeMessage);
                    break;
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                case Mensaje_Leido:
                    byte[] readBuf = (byte[]) msg.obj;//buffer de lectura...
                    //Construye un String de los bytes validos en el buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (D) Log.e(TAG, "Answer of Arduino: " + readMessage);
                    tvDato.setText("");
                    tvDato.setText( readMessage );
                    //RESULTADO_ARDUINO = tvDato.getText().toString().replace(" ", "");
                    if ( penultimoMsg.equals( "@" ) ){
                        penultimoMsg = "";
                        tvUsedWater.setText( readMessage + " L.");
                    }
                    else if ( penultimoMsg == "#" ){
                        penultimoMsg = "";
                        tvCountDown.setText( readMessage );
                    }
                    penultimoMsg = readMessage;
                    break;
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                case Mensaje_Nombre_Dispositivo:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME); //Guardamos nombre del dispositivo
                    Toast.makeText(getApplicationContext(), "Conectado con " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    tvInfConex.setText(R.string.msg_SiConex);
                    tvInfConex.setBackgroundColor(Color.parseColor("#FF00FF00"));
                    seleccionador = true;
                    break;
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                case Mensaje_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    tvInfConex.setText(R.string.msg_NoConex);
                    tvInfConex.setBackgroundColor(Color.parseColor("#FF0000"));
                    break;
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                case MESSAGE_Desconectado:
                    if (D) Log.e("Conexion", "Desconectados");
                    seleccionador = false;
                    break;
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            }//FIN DE SWITCH CASE PRIMARIO DEL HANDLER
        }//FIN DE METODO INTERNO handleMessage
    };//Fin de Handler


    @Override
    public void onBackPressed() {
        Stop();
        super.onBackPressed();
        this.finish();
    }
}

