package org.firstinspires.ftc.teamcode.examples.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * Purpose: Class for controlling the arm and claw
 * Author: Jonathan Ma, Ansh Gandhi
 */

@TeleOp(name = " 17-18 Season Arm")
@Disabled
public class seasonArm extends OpMode{

    // GO TO
    // seasonTeleop.JAVA TO
    // UPDATE VERSION NUMBER
    // BEFORE EVERY COMMIT

    // Initialize motor/servo variables
    private DcMotor armMotor;
    private Servo armClawLeft;
    private Servo armClawRight;
    private CRServo colorArm;
    private DigitalChannel touchSensor;

    // Initialize arm claw variables
    private double armLeftPosition = 0.35;
    private double armRightPosition = 0.65;

    // Initialize arm motor variables
    private final double armSpeedControl = 0.5;
    private double armMotorPower = 0;
    private double colorArmPower = 0;
    private int currentArmPosition = 0;

    // Create OpMode
    private OpMode op;
    public seasonArm(OpMode opmode) {
        op = opmode;
    }

    public void init() {

        op.telemetry.addData("Arm", "Initializing");

        // Initialize hardware variables
        armMotor = op.hardwareMap.dcMotor.get("armMotor");
        armClawLeft = op.hardwareMap.servo.get("clawLeft");
        armClawRight = op.hardwareMap.servo.get("clawRight");
        colorArm = op.hardwareMap.crservo.get("colorArm");
        touchSensor = op.hardwareMap.digitalChannel.get("touchSensor");

        // Set the digital channel to input
        touchSensor.setMode(DigitalChannel.Mode.INPUT);

        // Sets direction/zero power behavior of motor
        armMotor.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset encoder
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        op.telemetry.addData("Arm", "Initialized");
    }

    // Main Loop
    public void loop() {
        // This closes the arm claw when the left bumper is pressed
        if (op.gamepad2.left_bumper) {
            armLeftPosition = 0.7;
            armRightPosition = 0.3;
            op.telemetry.addData("Arm Servo Status", "Closed");
        }

        // This opens the arm claw completely when the right bumper button is pressed
        if (op.gamepad2.right_bumper) {
            armLeftPosition = 0.35;
            armRightPosition = 0.65;
            op.telemetry.addData("Arm Servo Status", "Open Completely");
        }

        if (op.gamepad2.a) {
            armLeftPosition = 0.5;
            armRightPosition = 0.5;
            op.telemetry.addData("Arm Servo Status", "Open Slightly");
        }

        // The left stick is used to raise and lower the arm
        armMotorPower = op.gamepad2.left_stick_y * armSpeedControl;

        // Stop arm's motion if it hits touch sensor and moving downward
        if (!touchSensor.getState()) {
            op.telemetry.addData("Touch Sensor", "Is Pressed");

            if (armMotorPower > 0) {
                armMotorPower = 0;
                op.telemetry.addData("Arm", "Is Stopped");
            }
        }

        // Stop arm's motion if it goes above upper limit and moving upward
        if (currentArmPosition < -5900 && armMotorPower < 0) {
            armMotorPower = 0;
            op.telemetry.addData("Arm Status", "Upper Limit Reached");
        }

        colorArmPower = op.gamepad2.right_stick_x;

        // The calculated power is then applied to the motors
        armMotor.setPower(armMotorPower);
        colorArm.setPower(colorArmPower);

        // Sets/Reads Servo Positions
        armClawLeft.setPosition(armLeftPosition);
        armClawRight.setPosition(armRightPosition);
        armLeftPosition = armClawLeft.getPosition();
        armRightPosition = armClawRight.getPosition();

        currentArmPosition = armMotor.getCurrentPosition();
        op.telemetry.addData("Arm Current Position", "%7d", currentArmPosition);

        //Prints servo/encoder positions
        op.telemetry.addData("Left Arm Servo Position", armLeftPosition);
        op.telemetry.addData("Right Arm Servo Position", armRightPosition);
    }
}