package view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.firstapp.R;
import com.mesibo.messaging.MesiboUI;

import data.AuthData;
import data.User;


public class MainActivity extends AppCompatActivity implements
        Mesibo.ConnectionListener,
        Mesibo.MessageListener {

    private User mRemoteUser;
    private Mesibo.UserProfile mProfile;

    private View mLoginButton1, mLoginButton2, mSendButton, mUiButton, mAudioCallButton, mVideoCallButton;
    private TextView mMessageStatus, mConnStatus;
    private EditText mMessage;

    private User mUser1 = AuthData.getUser1();
    private User mUser2 = AuthData.getUser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mLoginButton1 = findViewById(R.id.login1);
        mLoginButton2 = findViewById(R.id.login2);
        mSendButton = findViewById(R.id.send);
        mUiButton = findViewById(R.id.launchUI);
        mAudioCallButton = findViewById(R.id.audioCall);
        mVideoCallButton = findViewById(R.id.videoCall);
        mMessageStatus = findViewById(R.id.msgStatus);
        mConnStatus = findViewById(R.id.connStatus);
        mMessage = findViewById(R.id.message);

        mSendButton.setEnabled(false);
        mUiButton.setEnabled(false);
        mAudioCallButton.setEnabled(false);
        mVideoCallButton.setEnabled(false);
    }

    private void initChat(User user, User remoteUser) {
        Mesibo api = Mesibo.getInstance();
        api.init(getApplicationContext());

        Mesibo.addListener(this);
        Mesibo.setSecureConnection(true);
        Mesibo.setAccessToken(user.token);
        Mesibo.setDatabase("mydb", 0);
        Mesibo.start();

        mRemoteUser = remoteUser;
        mProfile = new Mesibo.UserProfile();
        mProfile.address = remoteUser.address;
        mProfile.name = remoteUser.name;
        Mesibo.setUserProfile(mProfile, false);

        // disable login buttons
        mLoginButton1.setEnabled(false);
        mLoginButton2.setEnabled(false);

        // enable buttons
        mSendButton.setEnabled(true);
        mUiButton.setEnabled(true);
        mAudioCallButton.setEnabled(true);
        mVideoCallButton.setEnabled(true);


        MesiboCall.getInstance().init(getApplicationContext());

        // Read receipts are enabled only when App is set to be in foreground
        Mesibo.setAppInForeground(this, 0, true);
        Mesibo.ReadDbSession mReadSession = new Mesibo.ReadDbSession(mRemoteUser.address, this);
        mReadSession.enableReadReceipt(true);
        mReadSession.read(100);
    }


    public void onLoginUser1(View view) {
        initChat(mUser1, mUser2);
    }

    public void onLoginUser2(View view) {
        initChat(mUser2, mUser1);
    }

    public void onSendMessage(View view) {
        Mesibo.MessageParams p = new Mesibo.MessageParams();
        p.peer = mRemoteUser.address;
        p.flag = Mesibo.FLAG_READRECEIPT | Mesibo.FLAG_DELIVERYRECEIPT;

        Mesibo.sendMessage(p, Mesibo.random(), mMessage.getText().toString().trim());
        mMessage.setText("");
    }

    public void onLaunchMessagingUi(View view) {
        MesiboUI.launchMessageView(this, mRemoteUser.address, 0);
    }

    public void onAudioCall(View view) {
        MesiboCall.getInstance().callUi(this, mProfile.address, false);
    }

    public void onVideoCall(View view) {
        MesiboCall.getInstance().callUi(this, mProfile.address, true);
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        mConnStatus.setText("Auth status: " + status);
    }

    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams messageParams, byte[] data) {
//        try {
//            String message = new String(data, "UTF-8");
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return true;
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams messageParams) {
        mMessageStatus.setText("Message status: " + messageParams.getStatus());
    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams messageParams, int i) {

    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams messageParams, Mesibo.Location location) {

    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams messageParams, Mesibo.FileInfo fileInfo) {

    }
}