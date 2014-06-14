package com.tanozaslan.acreditationapp;

public class calisan {

	private int id;
	private String isim;
	private int kimlikno;
	private String firma;
	private String gorev;
	private int bolge;
	private boolean localResimExist=false;
	private boolean iceride;
	
	//CONSTRUCTORS
	public calisan(int idField, String isimField, int kimliknoField, String firmaField, String gorevField, int bolgeField, boolean icerideField){
		setId(idField);
		seIsim(isimField);
		setKimlikNo(kimliknoField);
		setFirma(firmaField);
		setGorev(gorevField);
		setBolge(bolgeField);
		setIceride(icerideField);		
	}

	//SETS
	public void setId(int idField) {
		id=idField;		
	}
	public void seIsim(String isimField) {
		isim=isimField;		
	}
	public void setKimlikNo(int kimliknoField) {
		kimlikno=kimliknoField;		
	}
	public void setFirma(String firmaField) {
		firma=firmaField;		
	}
	public void setGorev(String gorevField) {
		gorev=gorevField;		
	}
	public void setBolge(int bolgeField) {
		bolge=bolgeField;		
	}
	public void setLocalResimExist(boolean localResimExistField){
		localResimExist=localResimExistField;
	}
	public void setIceride(boolean icerideField) {
		iceride=icerideField;		
	}

	//GETS
	public int getId(){
		return id;
	}
	public String getIsim(){
		return isim;
	}
	public int getKimlikno(){
		return kimlikno;
	}
	public String getFirma(){
		return firma;
	}
	public String getGorev(){
		return gorev;
	}
	public int getBolge(){
		return bolge;
	}
	public boolean isLocalResimExist(){
		return localResimExist;
	}
	public boolean getIceride(){
		return iceride;
	}

}
