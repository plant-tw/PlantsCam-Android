package plantscam.android.prada.lab.plantscamera;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by prada on 24/02/2018.
 */

@Scope
@Documented
@Retention(value= RetentionPolicy.RUNTIME)
public @interface UserScope {
}
