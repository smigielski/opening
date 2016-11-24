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
        ILLEGA_MOVE(-2,"X", false),
        UNKNOW_MOVE(-1,"-", false),
        NORMAL_MOVE(0,"", true),
        GOOD_MOVE(1,"!", true),
        POOR_MOVE(2,"?", false),
        VERY_GOOD_MOVE(3,"!!", true),
        VERY_POOR_MOVE(4,"??", false),
        SPECULATIVE_MOVE(5,"!?", true),
        QUESTIONABLE_MOVE(6,"?!", false),
        ;

        private final int status;
        private final String annotation;
        private final boolean acceptable;

        MoveStatus(int status, String annotation, boolean acceptable) {
            this.status = status;
            this.annotation = annotation;
            this.acceptable = acceptable;
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


        public static MoveStatus valueOfStatus(String san) {
            return null;
        }

        public boolean isAcceptable() {
            return acceptable;
        }

        public static MoveStatus valueOf(short nag) {
            for (MoveStatus moveStatus: values()){
                if (moveStatus.getStatus()==nag){
                    return moveStatus;
                }
            }
            return null;
        }
    }
    private MoveStatus moveStatus;
    private String message;

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }
}
