package com.sunilson.bachelorthesis.domain.authentication.interactors;

import com.sunilson.bachelorthesis.data.model.user.UserEntity;
import com.sunilson.bachelorthesis.data.repository.database.ApplicationDatabase;
import com.sunilson.bachelorthesis.domain.authentication.model.DomainLoginData;
import com.sunilson.bachelorthesis.domain.repository.AuthenticationRepository;
import com.sunilson.bachelorthesis.domain.shared.AbstractUseCase;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Linus Weiss
 *
 * Use case for logging in a certain user
 */
public class LoginUseCase extends AbstractUseCase<Boolean, LoginUseCase.Params> {

    private AuthenticationRepository authenticationRepository;
    private ApplicationDatabase applicationDatabase;

    @Inject
    public LoginUseCase(AuthenticationRepository authenticationRepository, ApplicationDatabase applicationDatabase) {
        this.authenticationRepository = authenticationRepository;
        this.applicationDatabase = applicationDatabase;
    }

    @Override
    protected Observable<Boolean> buildUseCaseObservable(Params params) {
        return authenticationRepository.signIn(new DomainLoginData(params.name, params.password))
                .map(userEntity -> {
            if(userEntity == null) return false;
            //Save user to local database
            applicationDatabase.applicationDao().addUser(userEntity);
            return true;
        });
    }

    public static final class Params {
        String name, password;

        private Params(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public static Params forLogin(String name, String password) {
            return new Params(name, password);
        }
    }
}
