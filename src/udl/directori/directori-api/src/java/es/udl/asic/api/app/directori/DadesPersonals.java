package es.udl.asic.api.app.directori;



public class DadesPersonals{
	
	private String uid;
	private String nom;
	private String cognoms;
	private String tlf;
	private String fax;
	private String webpersonal;
	private String ubicacio;
	private String correuprincipal;
	private String reenviament;
	private boolean guardacorreu;
	private String employeeType;
	private String adrecaAlternativa="";
	private String codi;
	private String dni;
	private String missatgeria;
	private String mobile;
	private String correuAlternatiu;
	private String estat;
	
	
	public String getUid(){
		return uid;
	}
	public void setUid(String uid){
		this.uid = uid;
	}
	public String getDni(){
		return dni;
	}
	public void setDni(String dni){
		this.dni = dni;
	}

	public String getCognoms() {
		return cognoms;
	}
	public void setCognoms(String cognoms) {
		this.cognoms = cognoms;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getTlf() {
		return tlf;
	}
	public void setTlf(String tlf) {
		this.tlf = tlf;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getWebpersonal() {
		return webpersonal;
	}
	public void setWebpersonal(String webpersonal) {
		this.webpersonal = webpersonal;
	}
	public String getEmployeeType (){
		return employeeType;
	}
	public void setEmployeeType(String employeeType){
		this.employeeType = employeeType;
	}

	public String getUbicacio(){
		return ubicacio;
	}
	
	public void setUbicacio(String ubicacio){
		this.ubicacio = ubicacio;
	}
	
	public void setCorreuprincipal(String correuprincipal){
		this.correuprincipal = correuprincipal;
	}
	
	public String getCorreuprincipal(){
		return correuprincipal;
	}
	
	public void setEstat(String estat){
		this.estat = estat;
	}
	
	public String getEstat(){
		return estat;
	}
	
	public void setCorreuAlternatiu(String correuAlternatiu){
		this.correuAlternatiu = correuAlternatiu;
	}
	
	public String getCorreuAlternatiu(){
		return correuAlternatiu;
	}
	
	public String getMissatgeria() {
		return missatgeria;
	}
	
	public void setMissatgeria(String missatgeria) {
		this.missatgeria = missatgeria;
	}
	
	public void setReenviament(String reenviament){
		this.reenviament= reenviament;
	}
	
	public String getReenviament(){
		return reenviament;
	}
	
	public void setGuardacorreu (boolean guardacorreu){
		this.guardacorreu = guardacorreu;
	}
	public boolean getGuardacorreu (){
		return guardacorreu;
	}
	
	public String getCodi(){
		return codi;
	}
	
	public void setCodi(String codi){
		this.codi=codi;
	}
	
	
	public boolean equals(Object obj) {
		DadesPersonals dp = (DadesPersonals) obj;
		
		if (dp.getNom().equals(nom) &&
			dp.getCognoms().equals(cognoms) &&
			dp.getTlf().equals(tlf) &&
			dp.getMobile().equals(mobile) &&
			dp.getFax().equals(fax) &&
			dp.getWebpersonal().equals(webpersonal) &&
			dp.getUbicacio().equals(ubicacio) &&
			dp.getMissatgeria().equals(missatgeria)
		){
			
			return true;
		}
		else{
			return false;
		}
	}
}