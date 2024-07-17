package ecommander.special.portal.outer;

/**
 * Результат выполнения запроса
 */
public class Result {
    public static final int SUCCESS = 0;
    public static final  int REQUEST_ERROR = 1;
    public static final  int CONNECTION_ERROR = 2;
    public static final  int RESPONSE_ERROR = 3;
    public static final  int OTHER_ERROR = 4;

    private int errorNum;
    private String errorMessage;
    private Request request;

    public Result(Request request, int errorNum, String errorMessage) {
        this.request = request;
        this.errorNum = errorNum;
        this.errorMessage = errorMessage;
    }

    public Request getRequest() {
        return request;
    }

    public int getErrorNum() {
        return errorNum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return errorNum == SUCCESS;
    }
}
