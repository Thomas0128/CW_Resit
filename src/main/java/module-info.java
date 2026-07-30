/**
 * Defines the 2048 JavaFX application module.
 *
 * <p>The module exposes the user interface, game model, engine,
 * history, artificial intelligence and level configuration packages.</p>
 */

module com.example.demo {
requires javafx.controls;
requires javafx.fxml;

opens com.example.demo to javafx.fxml;
exports com.example.demo;
exports com.example.demo.model;
exports com.example.demo.engine;
exports com.example.demo.history;
exports com.example.demo.ai;
exports com.example.demo.level;



}