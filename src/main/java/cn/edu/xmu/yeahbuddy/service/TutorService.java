package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TutorRepository;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TutorService implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @NonNls
    private static Log log = LogFactory.getLog(TutorService.class);

    private final YbPasswordEncodeService ybPasswordEncodeService;

    private final TutorRepository tutorRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param tutorRepository         Autowired
     * @param ybPasswordEncodeService Autowired
     */
    @Autowired
    public TutorService(TutorRepository tutorRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.tutorRepository = tutorRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

    /**
     * 强制转换对象至Tutor
     * 用于SpEL
     *
     * @param obj 对象
     * @return 强制转换为Tutor的对象
     */
    @Contract(pure = true)
    public static Tutor asTutor(Object obj) {
        return ((Tutor) obj);
    }

    /**
     * 对象是否为Tutor
     * 用于SpEL
     *
     * @param obj 对象
     * @return 对象是否为Tutor
     */
    @Contract(pure = true)
    public static boolean isTutor(Object obj) {
        return obj instanceof Tutor;
    }

    /**
     * 查找导师 提供{@link UserDetailsService#loadUserByUsername(String)}
     *
     * @param username 查找的导师用户名
     * @return 导师
     * @throws UsernameNotFoundException 找不到导师
     */
    @Override
    @Transactional(readOnly = true)
    public Tutor loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Trying to load Tutor " + username);
        Optional<Tutor> tutor = tutorRepository.findByUsername(username);
        if (!tutor.isPresent()) {
            log.info("Failed to load Tutor " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Tutor " + username);
        return tutor.get();
    }

    /**
     * 查找团队 代理{@link TutorRepository#findById(Object)}
     *
     * @param tutorId 查找的导师id
     * @return 团队
     */
    @Transactional(readOnly = true)
    public Optional<Tutor> findById(int tutorId) {
        log.debug("Finding Tutor by tutorId " + tutorId);
        return tutorRepository.findById(tutorId);
    }

    /**
     * 按ID查找导师
     *
     * @param id 查找的导师id
     * @return 导师
     * @throws IdentifierNotExistsException 找不到导师
     */
    @Transactional(readOnly = true)
    public Tutor loadById(int id) throws IdentifierNotExistsException {
        log.debug("Trying to load Tutor id " + id);
        Optional<Tutor> tutor = tutorRepository.findById(id);
        if (!tutor.isPresent()) {
            log.info("Failed to load Tutor id" + id + ": not found");
            throw new IdentifierNotExistsException("tutor.id.not_found", id);
        }
        log.debug("Loaded Tutor id " + id);
        return tutor.get();
    }

    /**
     * 查找导师 代理{@link TutorRepository#findByUsername(String)}
     *
     * @param username 查找的导师用户名
     * @return 导师
     */
    @NotNull
    @Transactional(readOnly = true)
    public Optional<Tutor> findByUsername(String username) {
        log.debug("Finding Tutor " + username);
        return tutorRepository.findByUsername(username);
    }

    /**
     * 查找所有导师
     *
     * @return 所有导师
     */
    @Transactional
    public List<Tutor> findAllTutors() {
        return tutorRepository.findAll();
    }

    /**
     * 注册导师
     *
     * @param dto 导师DTO
     * @return 新注册的导师
     * @throws IdentifierAlreadyExistsException 用户名已存在
     * @throws IllegalArgumentException         DTO中未填满所需信息
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTutor')")
    public Tutor registerNewTutor(TutorDto dto) throws IdentifierAlreadyExistsException {
        log.debug("Trying to register new Tutor " + dto.getUsername());

        if (!dto.ready()) {
            log.info("Failed to register Tutor " + dto.getUsername() + ": data not ready yet");
            throw new IllegalArgumentException("tutor.register.not_ready");
        }

        if (tutorRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.info("Failed to register Tutor " + dto.getUsername() + ": name already exist");
            throw new IdentifierAlreadyExistsException("tutor.name.exist", dto.getUsername());
        }

        Tutor tutor = new Tutor(dto.getUsername(), ybPasswordEncodeService.encode(dto.getPassword()));
        tutor.setEmail(dto.getEmail());
        tutor.setPhone(dto.getPhone());
        tutor.setDisplayName(dto.getDisplayName());

        Tutor result = tutorRepository.save(tutor);
        log.debug("Registered new Tutor " + result.toString());
        return result;
    }

    /**
     * 从PreAuthenticatedAuthenticationToken查找导师
     * 提供 {@link AuthenticationUserDetailsService#loadUserDetails(Authentication)}
     *
     * @param token {@link cn.edu.xmu.yeahbuddy.config.AuthTokenAuthenticationProvider#authenticate(Authentication)}
     * @return 导师
     */
    @Override
    public Tutor loadUserDetails(PreAuthenticatedAuthenticationToken token) {
        log.debug("Trying to load Tutor PreAuthenticatedAuthenticationToken " + token);
        return (Tutor) token.getPrincipal();
    }

    /**
     * 按ID删除导师
     *
     * @param id 管理员ID
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTutor')")
    public void deleteTutor(int id) {
        log.debug("Deleting Tutor " + id);
        tutorRepository.deleteById(id);
    }

    /**
     * 修改导师信息
     * 需要当前主体有ManageTutor权限或当前主体即为被修改用户
     *
     * @param id  导师ID
     * @param dto 导师DTO
     * @return 修改后的导师
     * @throws IdentifierAlreadyExistsException 如果修改用户名，用户名已存在
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTutor') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TutorService).isTutor(principal) && T(cn.edu.xmu.yeahbuddy.service.TutorService).asTutor(principal).id == #id)")
    public Tutor updateTutor(int id, TutorDto dto) {
        log.debug("Trying to update Tutor " + id);
        Tutor tutor = loadForUpdate(id);

        if (dto.getDisplayName() != null) {
            log.trace("Updated display name for Tutor " + id + ":" + tutor.getDisplayName() +
                              " -> " + dto.getDisplayName());
            tutor.setDisplayName(dto.getDisplayName());
        }

        if (dto.getEmail() != null) {
            log.trace("Updated email for Tutor " + id + ":" + tutor.getEmail() +
                              " -> " + dto.getEmail());
            tutor.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            log.trace("Updated phone for Tutor " + id + ":" + tutor.getPhone() +
                              " -> " + dto.getPhone());
            tutor.setPhone(dto.getPhone());
        }
        if (dto.getUsername() != null) {
            if (tutorRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.info("Fail to update username for Tutor " + tutor.getUsername() + ": username already exist");
                throw new IdentifierAlreadyExistsException("tutor.username.exist", dto.getUsername());
            } else {
                log.trace("Updated username for Tutor " + id + ":" + tutor.getUsername() +
                                  " -> " + dto.getUsername());
                tutor.setUsername(dto.getUsername());
            }
        }
        return tutorRepository.save(tutor);
    }

    /**
     * 修改导师密码
     * 需要当前主体有ManageTutor权限或当前主体即为被修改用户
     *
     * @param id          导师ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改后的导师
     * @throws BadCredentialsException 原密码不正确
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTutor') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TutorService).isTutor(principal) && T(cn.edu.xmu.yeahbuddy.service.TutorService).asTutor(principal).id == #id)")
    public Tutor updateTutorPassword(int id, CharSequence oldPassword, String newPassword) throws BadCredentialsException {
        log.info("Trying to update password for Tutor " + id);
        Tutor tutor = loadForUpdate(id);
        if (ybPasswordEncodeService.matches(oldPassword, tutor.getPassword())) {
            tutor.setPassword(ybPasswordEncodeService.encode(newPassword));
            log.info("Updated password for Tutor " + id);
            return tutorRepository.save(tutor);
        } else {
            log.warn("Failed to update password for Tutor " + id + ": old password doesn't match");
            throw new BadCredentialsException("tutor.update.password");
        }
    }

    /**
     * 重置导师密码
     * 需要当前主体有ManageTutor权限与ResetPassword权限
     *
     * @param id          导师ID
     * @param newPassword 新密码
     * @return 修改后的导师
     */
    @Transactional
    @PreAuthorize("hasAuthority('ResetPassword') && hasAuthority('ManageTutor')")
    public Tutor resetTutorPassword(int id, String newPassword) {
        Tutor tutor = loadForUpdate(id);
        log.info("Reset password for Tutor " + id);
        tutor.setPassword(ybPasswordEncodeService.encode(newPassword));
        return tutorRepository.save(tutor);
    }

    /**
     * 查找并加写锁
     * 用于读取并修改
     *
     * @param id 导师ID
     * @return 导师
     */
    @NotNull
    private Tutor loadForUpdate(int id) {
        Optional<Tutor> t = tutorRepository.queryById(id);

        if (!t.isPresent()) {
            log.info("Failed to load Tutor id" + id + ": not found");
            throw new IdentifierNotExistsException("tutor.id.not_found", id);
        }

        return t.get();
    }
}
