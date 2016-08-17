package com.libedi.mybatch.batch.jobs.job1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.libedi.mybatch.batch.jobs.job1.domain.Person;

/**
 * Processor Class
 * @author libedi
 *
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	private static final Logger logger = LoggerFactory.getLogger(PersonItemProcessor.class);
	
	@Override
	public Person process(Person person) throws Exception {
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();
		
		final Person transformedPerson = new Person(firstName, lastName);
		
		logger.info("Converting : ({}) into ({})", person, transformedPerson);
		
		return transformedPerson;
	}

}
