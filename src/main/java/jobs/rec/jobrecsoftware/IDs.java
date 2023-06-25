package jobs.rec.jobrecsoftware;

public class IDs {
    private int userID;
    private int companyID;
    private int jobID;
    private int applicationID;
    private int recommendationID;

    private static final IDs instance = new IDs();

    public static IDs getInstance(){
        return instance;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public int getRecommendationID() {
        return recommendationID;
    }

    public void setRecommendationID(int recommendationID) {
        this.recommendationID = recommendationID;
    }
}
