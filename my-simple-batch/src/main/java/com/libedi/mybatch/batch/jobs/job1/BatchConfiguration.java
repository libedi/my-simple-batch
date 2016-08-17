package com.libedi.mybatch.batch.jobs.job1;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.libedi.mybatch.batch.jobs.job1.domain.Person;

/**
 * Batch Configuration
 * - Batch는 Job으로 이루어져있고,
 * - Job은 Step으로 이루어져 있다.
 * - Step은 Reader - (Processor) - Writer 로 구성된다.
 * @author libedi
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
	// tag::readerwriterprocessor[]
	/**
	 * Reader
	 * @return
	 */
	@Bean
	public FlatFileItemReader<Person> reader() {
		FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("sample-data.md"));
		reader.setLineMapper(new DefaultLineMapper<Person>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "firstName", "lastName" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				});
			}
		});
		return reader;
	}
	
	/**
	 * Processor
	 * @return
	 */
	@Bean
	public PersonItemProcessor processor(){
		return new PersonItemProcessor();
	}
	
	/**
	 * Writer
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<Person> writer(){
		JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
		writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
		writer.setDataSource(dataSource);
		return writer;
	}
	// end::readerwriterprocessor[]
	
	// tag::listener[]
	/**
	 * Listener
	 * @return
	 */
	@Bean
	public JobExecutionListener listener(){
		return new JobCompletionNotificationListener(new JdbcTemplate(dataSource));
	}
	// end::listener[]
	
	// tag::jobstep[]
	/**
	 * Job Configuration
	 * @return
	 */
	@Bean
	public Job importUserJob(){
		return this.jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.flow(step1())
				.end()
				.build();
	}
	
	/**
	 * Step Configuration
	 * @return
	 */
	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.<Person, Person> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	// end::jobstep[]
	
}
