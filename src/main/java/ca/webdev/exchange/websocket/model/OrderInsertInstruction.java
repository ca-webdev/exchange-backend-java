package ca.webdev.exchange.websocket.model;

public class OrderInsertInstruction {

        private String instruction;

        public OrderInsertInstruction() {
        }

        public OrderInsertInstruction(String instruction) {
            this.instruction = instruction;
        }
        
        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }
}
