package insider.insider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static TextView chatPrint;
    static EditText chatScan;
    Button chatButton;

    // 서버 아이피
    String serverIp = "192.168.0.16";
    // 닉네임
    String name = "Lim";
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        chatPrint = (TextView) findViewById(R.id.chatPrint);
        chatScan = (EditText) findViewById(R.id.chatScan);
        chatButton = (Button) findViewById(R.id.chatButton);


        // 소켓 연결
        try {
            socket = new Socket(serverIp, 11054);
            Log.d("socket", "서버 연결");
            Thread sender = new Thread(new ClientSender(socket, name));
            Thread receiver = new Thread(new ClientReceiver(socket));
            sender.start();
            receiver.start();
        } catch (Exception e) {
        }
    }

    public void onButtonLogin(View view) {
        Intent TestIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(TestIntent);
    }

    static class ClientSender extends Thread {
        Socket socket;
        DataOutputStream out;
        String name;

        ClientSender(Socket socket, String name) {
            this.socket = socket;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                this.name = name;
            } catch (Exception e) {
            }
        }

        public void run() {
            try {
                if (out != null) {
                    out.writeUTF(name);
                }
                while (out != null) {
                    out.writeUTF("[" + name + "]" + chatScan.getText());
                }
            } catch (Exception e) {
            }
        }
    }

    static class ClientReceiver extends Thread {
        Socket socket;
        DataInputStream in;

        ClientReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
            } catch (Exception e) {
            }
        }

        public void run() {
            while (in != null) {
                try {
                    chatPrint.setText(in.readUTF());
                } catch (Exception e) {
                }
            }
        }
    }

    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (Exception e) {
        }
    }
}
