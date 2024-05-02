package com.pathing;


public class Odometry {
    // These are placeholders for the WPI classes ---------------------------------------------------------------
    public abstract class Encoder{
        public abstract int getCounts();
    }

    public abstract class DriveTrain{
        public abstract ChassisSpeeds getChassisSpeeds();
    }

    public class ChassisSpeeds{
        private double	omegaRadiansPerSecond; //Represents the angular velocity of the robot frame.
        private double	vxMetersPerSecond;	//Represents forward velocity w.r.t the robot frame of reference.
        private double	vyMetersPerSecond;  //Represents sideways velocity w.r.t the robot frame of reference.
    }

    public class Pose2D{
        private float x;
        private float y;
        private float omega;
    }

    public abstract class Gyro{
        public abstract float getAngle();
    }
    // end of placeholders --------------------------------------------------------------------------------------


    private Encoder leftEncoder;    //Tracks distance traveled along left side of robot
    private Encoder rightEncoder;   //Tracks distance traveled on right side of robot
    private Encoder backEncoder;    //Tracks distance traveled on back of robot
    private DriveTrain swerveChassis;
    private Gyro gyro;
    public Pose2D position;

    private final double CENTERTOLEFTENCODER = .2; //meters
    private final double CENTERTORIGHTENCODER = .2; //meters
    private final double CENTERTOBACKENCODER = .2; //meters
    private final double WHEELDIAMETER = 3 * Math.PI;
    private final int ENCODERTICKSPERREV = 1024;
    private final int LOOPTIME = 20; //ms

    private int lastLeftEncoderCount;
    private int lastRightEncoderCount;
    private int lastBackEncoderCount;
    private int leftEncoderCount;
    private int rightEncoderCount;
    private int backEncoderCount;
    private int dL;
    private int dR;
    private int dB;

    // Constructor
    public Odometry(Encoder leftEncoder, Encoder rightEncoder, Encoder backEncoder, Gyro gyro, DriveTrain swerveChassis) {
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.backEncoder = backEncoder;
        this.gyro = gyro;
        this.swerveChassis = swerveChassis;
        lastLeftEncoderCount = 0;
        lastRightEncoderCount = 0;
        lastBackEncoderCount = 0;
        leftEncoderCount = 0;
        rightEncoderCount = 0;
        backEncoderCount = 0;
    }

    public void update(){
        updateEncoders();
        ChassisSpeeds currentSpeed = getSpeeds();
        updatePose2D(currentSpeed);
    }

    public Pose2D getPose2D(){
        return position;
    }

    private void updateEncoders(){
        lastLeftEncoderCount = leftEncoderCount;
        lastRightEncoderCount = rightEncoderCount; 
        lastBackEncoderCount = backEncoderCount; 
        leftEncoderCount = -1 * leftEncoder.getCounts();
        rightEncoderCount = rightEncoder.getCounts();
        backEncoderCount = backEncoder.getCounts();

        dL = leftEncoderCount - lastLeftEncoderCount;
        dR = rightEncoderCount - lastRightEncoderCount;
        dB = backEncoderCount - lastBackEncoderCount;

    }

    public ChassisSpeeds getSpeeds( ){
        ChassisSpeeds out = new ChassisSpeeds();
        //assumes left wheel encoder is flipped
        out.omegaRadiansPerSecond = (((dR/CENTERTORIGHTENCODER - dL/CENTERTOLEFTENCODER)/ENCODERTICKSPERREV) * WHEELDIAMETER); //Difference of speeds of left and right divided by radius acting over
        out.vxMetersPerSecond = ((dR - dL)/(2 * ENCODERTICKSPERREV)) * WHEELDIAMETER;   // average of left and right encoders
        out.vyMetersPerSecond = dB - (out.omegaRadiansPerSecond * CENTERTOBACKENCODER);
        return out;
    }

    private Pose2D updatePose2D(ChassisSpeeds chassis){
        float rdx = (float)chassis.vxMetersPerSecond * LOOPTIME;
        float rdy = (float)chassis.vyMetersPerSecond * LOOPTIME;
        float fieldAngle = gyro.getAngle();

        position.x +=  Math.cos(fieldAngle) * rdx + Math.sin(fieldAngle) * rdy;
        position.y += Math.cos(fieldAngle) * rdy + Math.sin(fieldAngle) * rdx;
        position.omega += chassis.omegaRadiansPerSecond * LOOPTIME;
        return position;
    }

    

}