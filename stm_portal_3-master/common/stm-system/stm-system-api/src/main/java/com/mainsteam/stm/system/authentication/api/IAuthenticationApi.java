package com.mainsteam.stm.system.authentication.api;


import com.mainsteam.stm.system.authentication.bo.Authentication;

public interface IAuthenticationApi {
	void insert(Authentication authentication);
	Authentication getAuthentication();
}
