package cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager;

import java.security.Key;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;

public class JDOKeyProvider implements ResponseKeyProvider {

	private static final Key k = Utils
			.deserialize(
					Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAACejCCAnYCAQAwDQYJKoZIhvcNAQEBBQAEggJgMIICXAIBAAKBgQCY2xNo1sZm1zRQC03yrve5bYGmftl+qr8izETJ7EJWP9G8wYq4ogb7KyjJhFec1XXGBrX0BnF2ZjJwJtHOnblPG9jrBpQl2D192RjYKFMdda/DNDMocpNMxMkQFX1AUne50Fj74FeiNwH5Tj9FErTDPXYCxphKqLBZrRxJQtYfnQIDAQABAoGAHPJn85Ow0OrySjJJ2aqO2TvGsLwW/ijht3pNkJvAWEsPshpI5fwxLEGZIMiCOv4lppHAxWyu8ggahyii1OkDnY9VSbaL02LEjD/WorA37HkXrWA3wCYiNAu3fANElkGUv5hpecLO2Ove0gE2602Druau/JW000TvjIo7U3SFF9UCQQDaI8y1yo8jBfo8cI4kiN21YeYiLUXTgRrIBo10GJ3/EY1K71qR/4CvqkAGUIHTh5UiCcIQv9qb6dNSJdPkHcLjAkEAs2KhcafS7RNlZ//W26h9PVIKiL+kqSE1OjeING385RYono4oHgtSdh/jsGjH0ScwkyS74THVxBHNlZuP9hGbfwJAYxKhVetyiQCc/zhLhpJWx5t4Dwuqy/218STwt8q6b29EUcVVyDtX8fCL9ZI49J5+gCAYcM0B95ACNywRCWju6wJATx4cSI2VeRzJ6AGt2PnKKwaJQOENlc7gtAmUGFO+a6fHSI84YG/r8c+E0+SS5MudH/jeqKcbOjwD6Y1QBF6lrwJBAIkVb4J3yyUpX0TIbvveKsVH6sY1A9qM3sDjpccVN55JSSUULwjZEWTRSW96W/tp7lrMRz+uzUiAdo/++CMAgrd0AAZQS0NTIzh+cgAZamF2YS5zZWN1cml0eS5LZXlSZXAkVHlwZQAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQAB1BSSVZBVEU="),
					Key.class);

	@Override
	public Key getResponseEncryptionKey(LicenseRequest request) {
		return k;
	}

}