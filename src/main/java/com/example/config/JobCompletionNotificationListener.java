package com.example.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.entity.User;
import com.example.model.UserRepository;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	@Autowired
	private UserRepository userRepository;

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			Pageable pageable;
			
			//printing first 10 pages for the record
			for (int i = 0; i <10; i++) {
				
				System.out.println("\n\t\t\t Page "+i+ "\t\t\t\n");
				pageable = PageRequest.of(i, 10);

				Page<User> page = userRepository.findAll(pageable);

				if (page.hasContent()) {
					List<User> list = page.getContent();
					list.forEach(System.out::println);
				} else {
					System.out.println("Given page not exist");
				}
				
			}

		}
	}
}