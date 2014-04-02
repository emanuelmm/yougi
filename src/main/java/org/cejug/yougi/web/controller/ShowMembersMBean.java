package org.cejug.yougi.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Province;
import org.cejug.yougi.entity.UserAccount;

@Named("showMembersMBean")
@RequestScoped
public class ShowMembersMBean {
	
	private List<List<UserAccount>> memberRows;
	
	@EJB
	private UserAccountBean userAccountBean;
	
	@PostConstruct
	public void init(){
		memberRows = new ArrayList<>();
		List<UserAccount> userAccounts = new ArrayList<>();
		for (UserAccount account : userAccountBean.findAllActiveAccounts() ) {
			if(memberRows.size() == 0)
				memberRows.add( userAccounts );
			
			if(userAccounts.size() < 3)
				userAccounts.add(account);
			else{
				userAccounts = new ArrayList<>();
				memberRows.add( userAccounts );
				userAccounts.add(account);
			}
		}
	}
	
	public String formatedAddress(UserAccount member){
		StringBuilder sb = new StringBuilder();
		City city = member.getCity();
		if(city != null){
			sb.append( city.getName() );
		}
		Province province = member.getProvince();
		if(province != null){
			if(city != null){
				sb.append(", ");
			}
			sb.append(province.getName());
		}
		return sb.toString();
	}
	
	public boolean showAddress(UserAccount member){
		return member.getCity() != null || member.getCountry() != null || member.getProvince() != null;
	}
	
	public boolean showWebsite(UserAccount member){
		return member.getWebsite() != null;
	}
	
	public boolean showTwitter(UserAccount member){
		return member.getTwitter() != null;
	}
	
	public List<List<UserAccount>> getMemberRows() {
		return memberRows;
	}

}
