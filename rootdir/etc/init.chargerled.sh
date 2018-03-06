#!/vendor/bin/sh
# This service uses polling to control the battery light while charging offline.

export PATH="/system/bin"

# Define used paths:
CAPACITY_PATH="/sys/devices/qpnp-charger-14/power_supply/battery/capacity"

RED_LED_FILE="/sys/class/leds/red/brightness";
GREEN_LED_FILE="/sys/class/leds/green/brightness";

RED_PWM_FILE="/sys/class/leds/red/pwm_us";
GREEN_PWM_FILE="/sys/class/leds/green/pwm_us";

# Initialization:
echo 100 > $RED_PWM_FILE
echo 100 > $GREEN_PWM_FILE
echo 0 > $RED_LED_FILE
echo 0 > $GREEN_LED_FILE

# Polling loop:
while true
do
    READ_CAPACITY="$(cat $CAPACITY_PATH)"

    if [ "$READ_CAPACITY" -ge 0 ] && [ "$READ_CAPACITY" -le 15 ]; then
        # Red
        echo 0 > $GREEN_LED_FILE
        echo 50 > $RED_LED_FILE
    elif [ "$READ_CAPACITY" -ge 16 ] && [ "$READ_CAPACITY" -le 89 ]; then
        # Orange
        echo 50 > $GREEN_LED_FILE
        echo 50 > $RED_LED_FILE
    elif [ "$READ_CAPACITY" -ge 90 ]; then
        # Green
        echo 0 > $RED_LED_FILE
        echo 100 > $GREEN_LED_FILE
        break
    fi

    # Update every 15 seconds
    sleep 15
done

exit 0
