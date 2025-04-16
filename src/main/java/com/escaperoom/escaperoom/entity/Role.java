package com.escaperoom.escaperoom.entity;

import static com.escaperoom.escaperoom.constant.Authority.ADMIN_AUTHORITIES;
import static com.escaperoom.escaperoom.constant.Authority.HR_AUTHORITIES;
import static com.escaperoom.escaperoom.constant.Authority.MANAGER_AUTHORITIES;
import static com.escaperoom.escaperoom.constant.Authority.SUPER_ADMIN_AUTHORITIES;
import static com.escaperoom.escaperoom.constant.Authority.USER_AUTHORITIES;

public enum Role {

	ROLE_USER(USER_AUTHORITIES),
	ROLE_HR(HR_AUTHORITIES),
	ROLE_MANAGER(MANAGER_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES),
	ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);
	
	private String[] authorities;
	
	Role(String... authorities){
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
}
