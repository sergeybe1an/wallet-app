package com.sbelan.walletapp.component;

import com.sbelan.walletapp.model.dto.UserDto;
import com.sbelan.walletapp.repository.UserRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Loader for users from resources/users.csv
 */
@Slf4j
@Component
public class UsersData {
    private static final String COMMA = ",";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    @Value("${users.filepath}")
    private String usersFilepath;

    private UserRepository userRepository;

    private ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void walletAppIsReady(ApplicationReadyEvent event) throws IOException {
        List<UserDto> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return;
        }

        Resource resource = resourceLoader.getResource(String.format("classpath:%s", usersFilepath));
        if (!resource.exists()) {
            throw new IllegalArgumentException(String.format("File %s didn't found!", usersFilepath));
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String record;
            while ((record = bufferedReader.readLine()) != null) {
                String[] values = record.split(COMMA);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
                UserDto userDto = UserDto.builder()
                    .id(Long.parseLong(values[0]))
                    .nickName(values[1])
                    .balance(BigDecimal.valueOf(Long.parseLong(values[2])))
                    .createTime(LocalDateTime.parse(values[3], formatter))
                    .updateTime(LocalDateTime.parse(values[4], formatter))
                    .build();

                userRepository.save(userDto);
                log.info("insert user with id {} and nickname {} to db", userDto.getId(),
                    userDto.getNickName());
            }
        } catch (Exception e) {
            log.error("Error while reading file with users data: {}", e.getMessage());
        }
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
