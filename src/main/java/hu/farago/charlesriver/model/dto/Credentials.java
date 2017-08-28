package hu.farago.charlesriver.model.dto;

public class Credentials {

	public String protocol = "https";
	public String hostname = "test.ms.crd.com";
	public Integer port = 8081;

	public String username = "BVARGA";
	public String password = "Ii1234";

	public String query = "Active orders for a Ref ID";
	public Integer basket = 10385273;
	public String trader = "BVARGA";
	public String supervisor = "GCZACHESZ";

	public Credentials() {
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port + "";
	}

	public void setPort(String port) {
		this.port = Integer.getInteger(port);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getBasket() {
		return basket + "";
	}

	public void setBasket(String basket) {
		this.basket = Integer.getInteger(basket);
	}

	public String getTrader() {
		return trader;
	}

	public void setTrader(String trader) {
		this.trader = trader;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	
}
