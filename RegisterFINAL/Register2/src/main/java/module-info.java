module com.esa.moviestar {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires java.sql;
    requires java.desktop;


    opens com.esa.moviestar to javafx.fxml;
    exports com.esa.moviestar.Database;
    opens com.esa.moviestar.Database to javafx.fxml;
    exports com.esa.moviestar.Profile;
    opens com.esa.moviestar.Profile to javafx.fxml;
    exports com.esa.moviestar;
    exports com.esa.moviestar.Login;
    opens com.esa.moviestar.Login to javafx.fxml;
    exports com.esa.moviestar.movie_view;
    opens com.esa.moviestar.movie_view to javafx.fxml;
    exports com.esa.moviestar.home;
    opens com.esa.moviestar.home to javafx.fxml;
}