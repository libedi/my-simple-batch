package com.libedi.mybatch.batch.jobs.job1.domain;

public class Person {
	private String lastName;
	private String firstName;
	
	public Person() {
	}
	public Person(String lastName, String firstName) {
		super();
		this.lastName = lastName;
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Override
	public String toString() {
		return "lastName=" + lastName + ", firstName=" + firstName;
	}
	
}