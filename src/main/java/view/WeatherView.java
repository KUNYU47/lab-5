package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import interface_adapter.weather.WeatherController;
import interface_adapter.weather.WeatherState;
import interface_adapter.weather.WeatherViewModel;
import interface_adapter.weather_daily.WeatherDailyViewModel;

/**
 * The View for when the user is on Weather Use Case.
 */
public class WeatherView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "weather";
    private final WeatherViewModel weatherViewModel;
    private WeatherController weatherController;

    // UI components
    private final JTextField cityNameInputField = new JTextField(15);
    private final JLabel cityLabel = new JLabel(WeatherViewModel.CITY_INFO_LABEL);
    private final JLabel temperatureLabel = new JLabel(WeatherViewModel.TEMPERATURE_LABEL);
    private final JLabel conditionLabel = new JLabel(WeatherViewModel.CONDITION_LABEL);
    private final JLabel errorMessageLabel = new JLabel();
    private final JButton getWeatherButton;
    private final JButton goToSettingsButton;

    // Constructor
    public WeatherView(WeatherViewModel weatherViewModel) {

        this.weatherViewModel = weatherViewModel;
        this.weatherViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(WeatherViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create input field for city name.
        final LabelTextPanel cityInputPanel = new LabelTextPanel(
                new JLabel(WeatherViewModel.CITY_LABEL), cityNameInputField);

        // Create drop-down menu to switch between modes.
        final String[] modeOptions = new String[] {WeatherViewModel.CURRENT,
                                                   WeatherViewModel.HOURLY,
                                                   WeatherViewModel.DAILY};
        final JComboBox modeComboBox = new JComboBox(modeOptions);
        modeComboBox.setSelectedItem(WeatherDailyViewModel.CURRENT);

        // Create a panel to combine city input field and the drop-down menu.
        final JPanel cityInputPlusModePanel = new JPanel();
        cityInputPlusModePanel.setLayout(new BoxLayout(cityInputPlusModePanel, BoxLayout.X_AXIS));
        cityInputPlusModePanel.add(cityInputPanel);
        cityInputPlusModePanel.add(modeComboBox);

        final JPanel weatherInfoPanel = new JPanel();
        weatherInfoPanel.setLayout(new BoxLayout(weatherInfoPanel, BoxLayout.Y_AXIS));
        weatherInfoPanel.add(cityLabel);
        weatherInfoPanel.add(temperatureLabel);
        weatherInfoPanel.add(conditionLabel);
        weatherInfoPanel.add(errorMessageLabel);

        final JPanel buttons = new JPanel();
        getWeatherButton = new JButton(WeatherViewModel.GET_WEATHER_BUTTON_LABEL);
        buttons.add(getWeatherButton);
        goToSettingsButton = new JButton(WeatherViewModel.SETTINGS_LABEL);
        buttons.add(goToSettingsButton);

        // Add action listener to the "Get Weather" button
        getWeatherButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        final String cityName = cityNameInputField.getText();
                        weatherController.execute(cityName);
                    }
                });

        goToSettingsButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        weatherController.switchToLoggedInView();
                    }
                });

        modeComboBox.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        final String mode = (String) modeComboBox.getSelectedItem();
                        if (mode.equals(WeatherViewModel.HOURLY)) {
                            weatherController.switchToHourlyView();
                        }
                        else if (mode.equals(WeatherViewModel.DAILY)) {
                            weatherController.switchToDailyView();
                        }
                        modeComboBox.setSelectedItem(WeatherDailyViewModel.CURRENT);
                    }
                }
        );

        // Set up the main layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(cityInputPlusModePanel);
        this.add(buttons);
        this.add(weatherInfoPanel);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    // Note that this method is un used thus can be deleted.
    public void actionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Settings not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final WeatherState state = (WeatherState) evt.getNewValue();
        setFields(state);
        if (state.getErrorMessage() != null) {
            errorMessageLabel.setText(state.getErrorMessage());
            JOptionPane.showMessageDialog(this, errorMessageLabel);
        }
    }

    private void setFields(WeatherState state) {
        cityNameInputField.setText("");
        cityLabel.setText(WeatherViewModel.CITY_INFO_LABEL + state.getCity());
        temperatureLabel.setText(WeatherViewModel.TEMPERATURE_LABEL + state.getTemperature());
        conditionLabel.setText(WeatherViewModel.CONDITION_LABEL + state.getCondition());
    }

    public String getViewName() {
        return viewName;
    }

    public void setWeatherController(WeatherController weatherController) {
        this.weatherController = weatherController;
    }
}
