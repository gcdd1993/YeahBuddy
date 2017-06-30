package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.TutorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.TokenService;
import cn.edu.xmu.yeahbuddy.service.TutorService;
import org.jetbrains.annotations.NonNls;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(final AdministratorRepository administratorRepository,
                           final AdministratorService administratorService,
                           final TeamRepository teamRepository,
                           final TeamService teamService,
                           final TutorRepository tutorRepository,
                           final TutorService tutorService,
                           final TokenService tokenService) {
        return args -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            if(administratorRepository.findByName("admin") == null){

                SecurityContextHolder.getContext().setAuthentication(ultimate);
                administratorService.registerNewAdministrator(
                        new AdministratorDto()
                                .setName("admin")
                                .setPassword("admin")
                                .setAuthorities(
                                        Arrays.stream(AdministratorPermission.values())
                                              .map(AdministratorPermission::name)
                                              .collect(Collectors.toSet())),
                        ultimate);
                SecurityContextHolder.getContext().setAuthentication(null);
            }
            int teamId = 0;
            if(teamRepository.findByName("team") == null){
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                Team team = teamService.registerNewTeam(
                        new TeamDto()
                                .setName("team")
                                .setPassword("team")
                                .setEmail("a@b.com")
                                .setPhone("18988888888")
                                .setProjectName("yeahbuddy"));
                teamId = team.getId();
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            if(tutorRepository.findByName("tutor") == null){
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                Tutor tutor = tutorService.registerNewTutor(
                        new TutorDto()
                                .setName("tutor")
                                .setPassword("tutor")
                                .setEmail("c@b.com")
                                .setPhone("13988888888"));
                @NonNls String token = tokenService.createToken(tutor, 1, Collections.singletonList(teamId));

                SecurityContextHolder.getContext().setAuthentication(null);

                System.out.println("Token created: " + token);
            }
        };

    }

}