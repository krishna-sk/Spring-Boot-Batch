package com.example.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.entity.User;
import com.example.model.UserModel;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	private final String[] FIELD_NAMES = new String[] { "id", "first_name", "last_name", "email", "gender",
			"ip_address","street_number" };

	@Autowired
	private DataSource dataSource;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	JobCompletionNotificationListener listener;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	/*
	 * If we want to exclude reading some fields(columns) from the csv file we can do it by using 
	 * either using ineTokenizer.setIncludedFields(new int[] {index of the fields we need to include}) or 
	 * we can omit them in the batch processor
	 */
	
//	 @Bean
//	public FlatFileItemReader<UserModel> reader() {
//		FlatFileItemReader<UserModel> reader = new FlatFileItemReader<>();
//		reader.setResource(new ClassPathResource("Records.csv"));
//		reader.setLineMapper(getLineMapper());
//		reader.setLinesToSkip(1);
//
//		return reader;
//	}
//
//		private LineMapper<UserModel> getLineMapper() {
//
//		DefaultLineMapper<UserModel> lineMapper = new DefaultLineMapper<>();
//		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
//
//		lineTokenizer.setNames(FIELD_NAMES);
//		lineTokenizer.setIncludedFields(new int[] {0, 1, 2, 3, 4, 5, 6});
//		lineMapper.setLineTokenizer(lineTokenizer);
//
//		BeanWrapperFieldSetMapper<UserModel> fieldSetMapper = new BeanWrapperFieldSetMapper<UserModel>();
//		fieldSetMapper.setTargetType(UserModel.class);
//		lineMapper.setFieldSetMapper(fieldSetMapper);
//		return lineMapper;
//	}
	

	@Bean
    public FlatFileItemReader<UserModel> reader() {
        return new FlatFileItemReaderBuilder<UserModel>().name("UserModelItemReader")
                .resource(new ClassPathResource("Records.csv")).linesToSkip(1)
                .delimited().names(FIELD_NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<UserModel>() {
                    {
                        setTargetType(UserModel.class);
                    }
                }).build();
    }
	
	@Bean
	public UserItemProcessor processor() {
		return new UserItemProcessor();
	}
	
	
//	@Bean
//	public JdbcBatchItemWriter<User> writer() {
//
//		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<User>();
//		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
//		writer.setSql("insert into user(id,first_name,last_name,email,gender,ip_address,street_number) "
//				+ "values(:id,:firstName,:lastName,:email,:gender,:ipAddress,:streetNumber)");
//		writer.setDataSource(dataSource);
//		return writer;
//	}
	
	@Bean
    public JdbcBatchItemWriter<User> writer() {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>())
                .sql("INSERT INTO user(id,first_name,last_name,email,gender,ip_address,street_number) "
                		+ "values(:id,:firstName,:lastName,:email,:gender,:ipAddress,:streetNumber)")
                .dataSource(dataSource).build();
    }
 
	@Bean
	public Job importUserJob() {

		return jobBuilderFactory.get("user-data-insert-job").incrementer(new RunIdIncrementer()).
				listener(listener).flow(step1()).end().build();
	}
	
	

	@Bean
	public Step step1() {

		return stepBuilderFactory.get("insert-user-step").<UserModel, User>chunk(100).reader(reader())
				.processor(processor()).writer(writer()).build();
	}

}
