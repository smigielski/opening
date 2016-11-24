package pl.linuh.opening.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by marek on 24/11/2016.
 */
public class ResponseMessage {
    public ResponseMessage(MoveStatus normalMove) {
        this.moveStatus=normalMove;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum MoveStatus {
        UNKNOW_MOVE(-1,"-"),
        NORMAL_MOVE(0,""),
        GOOD_MOVE(1,"!"),
        POOR_MOVE(2,"?"),
        VERY_GOOD_MOVE(3,"!!"),
        VERY_POOR_MOVE(4,"??"),
        SPECULATIVE_MOVE(5,"!?"),
        QUESTIONABLE_MOVE(6,"?!"),
        ;

        private final int status;
        private final String annotation;

        MoveStatus(int status, String annotation) {
            this.status = status;
            this.annotation = annotation;
        }
        public String getName(){
            return this.name();
        }

        public int getStatus() {
            return status;
        }

        public String getAnnotation() {
            return annotation;
        }


    }
    private MoveStatus moveStatus;
    private String message;

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }
}
