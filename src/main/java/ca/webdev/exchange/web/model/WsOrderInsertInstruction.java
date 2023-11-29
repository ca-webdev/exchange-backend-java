package ca.webdev.exchange.web.model;

public class WsOrderInsertInstruction {

        private String instruction;

        public WsOrderInsertInstruction() {
        }

        public WsOrderInsertInstruction(String instruction) {
            this.instruction = instruction;
        }
        
        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }
}
