package com.kkp.nure.animalrescue.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.AnimalInfoActivity;

import java.util.List;

public class ScanQrFragment extends CustomFragment implements DecoratedBarcodeView.TorchListener {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DecoratedBarcodeView barcodeView;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String text = result.getText();
            if (text == null) return;

            if((!text.startsWith("http://") && !text.startsWith("https://")) || !text.contains("/animal/")) {
                Toast.makeText(getContext(), R.string.unknown_qr_code_format, Toast.LENGTH_SHORT).show();
                return;
            }

            String[] textSplit = text.split("/animal/");
            if(textSplit.length < 2) {
                Toast.makeText(getContext(), R.string.unknown_qr_code_format, Toast.LENGTH_SHORT).show();
                return;
            }

            long animalId;
            try {
                animalId = Long.parseLong(textSplit[1]);
            } catch(NumberFormatException e) {
                Toast.makeText(getContext(), R.string.unknown_qr_code_format, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), AnimalInfoActivity.class);
            intent.putExtra("animalId", animalId);
            requireContext().startActivity(intent);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    public ScanQrFragment() {
        super(R.layout.fragment_scan_qr);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_qr, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);
        barcodeView.setTorchListener(this);

        checkCameraPermission();

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startScanner();
        }
    }

    private void startScanner() {
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(getContext(), R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTorchOn() {}
    @Override
    public void onTorchOff() {}
}
