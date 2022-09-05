package com.ecommerce.imobiliaria.Services;

import com.ecommerce.imobiliaria.Models.User;
import com.ecommerce.imobiliaria.Registro.Token.ConfirmationToken;
import com.ecommerce.imobiliaria.Registro.Token.ConfirmationTokenRepository;
import com.ecommerce.imobiliaria.Registro.Token.ConfirmationTokenService;
import com.ecommerce.imobiliaria.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.constant.Constable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "Usuário: %s não encontrado";
    private static final String USER_NOT_FOUND_MSG_ID = "Usuário: não encontrado";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private final RoleService roleService;

    public List<User> listar() {
        return userRepository.findAll();
    }


    public String verificaUsername(String name){
        return userRepository.verificaUsername(name);
    }
    public String verificaIdentificacao(String identificacao){
        return userRepository.verificaIdentificacao(identificacao);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
                 if(user.isEmpty()) {
                     throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username));
                 } else {
                    log.info("Usuário: {} encontrado", username);
                 }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                 user.get().getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
                 return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
    }

    public String signUpUser(User user) {
        boolean userExists = userRepository.findByUsername(user.getUsername()).isPresent();

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                null,
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                null,
                user

        );
        if (userExists) {
            User userExistente = userRepository.findByUsername(user.getUsername()).get();
            boolean enabled = userExistente.isEnabled();
            if (!enabled) {
                String token1 = confirmationTokenRepository.findByUserId(userExistente.getIdUser()).get().getToken();
                confirmationTokenService.setExpirouEm(token1);
                return token1;
            }else {
                throw new IllegalStateException("Usuário já existe");

            }
        }  else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setDataCriacao(LocalDateTime.now());
            user.setDataAtualizacao(LocalDateTime.now());
            userRepository.save(user);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
            return token;
        }


    }


    public User promoverParaAdmin(Integer idUser) {
        User user = userRepository.findById(idUser).orElse(null);
        if (user != null) {
            roleService.salvarRoleNoUser("ADMIN", user.getUsername());
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG_ID);
        }
        return user;
    }


    public void promoverParaVendedor(Integer idUser) {
        User user = userRepository.findById(idUser).orElse(null);
        if (user != null) {
            roleService.salvarRoleNoUser("VENDEDOR", user.getUsername());
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG_ID);
        }
    }


    public int enableUser(String email){
        return userRepository.enableUser(email);
    }

    //delete user
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.getRoles().clear();
            userRepository.delete(user);
        } else {
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG_ID);
        }
    }

    public User findById(Integer idUser) {
        return userRepository.findById(idUser).orElseThrow( () -> new EntityNotFoundException(USER_NOT_FOUND_MSG_ID));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow( () -> new EntityNotFoundException(USER_NOT_FOUND_MSG_ID));
    }


    public User updateUser(User user) {
        User userExistente = userRepository.findById(user.getIdUser()).orElse(null);
        if (userExistente != null) {


            findById(user.getIdUser());
            user.setDataAtualizacao(LocalDateTime.now());
            user.setDataCriacao(userExistente.getDataCriacao());
            user.setPassword(userExistente.getPassword());
            user.setEnabled(userExistente.isEnabled());
            user.setRoles(userExistente.getRoles());
            user.setUsername(userExistente.getUsername());
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG_ID);
        }
    }


    public List<User> findUsersByRoleId(Integer idRole) {
        return userRepository.findUserByRoleId(idRole);
    }


    public User disabilitarHabilitarConta(String username) {
        User user = userRepository.findByUsername(username).orElseThrow( () -> new EntityNotFoundException(USER_NOT_FOUND_MSG_ID));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return null;
    }


    public List<User> findUsersRegistradoPorMes(Integer idRole) {
        return (List<User>) userRepository.findUsersRegistradoPorMes(idRole);
    }



    public Integer findTotalSignedUpByRole(Integer idRole) {
        return userRepository.findTotalSignedUpByRole(idRole);
    }
}
