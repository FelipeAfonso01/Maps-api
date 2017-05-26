package com.example.aluno.mapas;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // declara a latitude e longitude do marcador
    double lat = -25.472597;
    double longi = -49.252206;
    LatLng ll = new LatLng(lat, longi);

    double lat_user = 0.0;
    double longi_user = 0.0;

    Button btnMapa;
    Integer tipo=1;
    String mapa;


    private Marker marcador;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtem o SupportMapFragment e é notificado quando o mapa está pronto para uso
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // esse switch serve para alterar o tipo de mapa
                switch(tipo){
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mapa = "HÍBRIDO";
                        tipo=2;
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mapa = "SATÉLITE";
                        tipo=3;
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mapa = "TERRENO";
                        tipo=4;
                        break;
                    case 4:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapa = "NORLMAL";
                        tipo=1;
                        break;
                }
                Toast.makeText(MapsActivity.this,mapa,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adiciona a marca abaixo e coloca como titulo do ponteiro "Concessionária Switch"
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title("Concessionária Switch"));

        // define o zoom
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(ll, 20);

        // move a camera para localizaçao que acabou de receber a latitude, longitudo e o zoom
        mMap.moveCamera(location);

        // realiza as validações para retornar a localização atual
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Coloca os controles de zoom, onde permite ampliar e diminuir o zoom do mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // chama o método minhalocalizacao
        minhalocalizacao();

    }

    // Esse método serve para colocar o marcador no mapa incluimos a latitude e longitude do usuáriono Latlng e com o camera update levamos o foco para este ponteiro
    private void colocarmarcador(double lat_user, double longi_user) {
        LatLng coordenadas = new LatLng(lat_user, longi_user);
        CameraUpdate localizacao = CameraUpdateFactory.newLatLngZoom(coordenadas, 20);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Sua Localização")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.felipe)));
        mMap.animateCamera(localizacao);

    }


    // esse método servirá para conseguir a latitude e longitude do usuário, verificamos se é diferente de nulo para não parar a aplicação, e logo após receber envia para colocar o marcador
    private void atualizarlocal(Location locations) {
        if (locations != null) {
            lat_user = locations.getLatitude();
            longi_user = locations.getLongitude();
            colocarmarcador(lat_user, longi_user);
        }
    }


    // criamos um objeto do tipo Location Listene, no qual tem a função de estar sempre atento para os sinais de gps recebidos
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            atualizarlocal(location);
        }
    };


    // Criaremos uum método que vai fazer referencia ao Location Maneger, no qual é ultilizado para obter os serviços de geoposicionamento no dispositivo
    private void minhalocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        atualizarlocal(location);

    }
}
