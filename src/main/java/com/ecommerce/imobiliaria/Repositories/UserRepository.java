package com.ecommerce.imobiliaria.Repositories;

import com.ecommerce.imobiliaria.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.lang.constant.Constable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

        Optional<User> findByUsername(String username);

        @Transactional
        @Modifying
        @Query("UPDATE User u SET u.enabled = true WHERE u.username = ?1")
        int enableUser(String email);

        @Query(value = "SELECT * FROM user, user_role WHERE user_role.id_role = :id AND user.id_user = user_role.id_user", nativeQuery = true)
        List<User> findUserByRoleId(Integer id);


        //call to procedure
        @Query(value = "CALL ecommerceimobiliaria.usersSignedUpPerMonth(?1)", nativeQuery = true)
        List<?> findUsersRegistradoPorMes(@Param("idRole") Integer idRole);

        @Query(value = "SELECT COUNT(*) FROM user_role WHERE user_role.id_role = :idRole", nativeQuery = true)
        Integer findTotalSignedUpByRole(Integer idRole);

        @Query(value = "SELECT username FROM ecommerceimobiliaria.user\n" +
                        "WHERE username = :name", nativeQuery = true)
        String verificaUsername(String name);

        @Query(value="SELECT identificacao FROM ecommerceimobiliaria.user \n" +
                "WHERE identificacao = :identificacao", nativeQuery = true)
        String verificaIdentificacao(String identificacao);

        }
