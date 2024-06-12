package main.compilador.cmp.analisador.contextual;

import java.util.ArrayList;
import java.util.List;

public class IdentificationTable {
    private List<String> ids;
    private List<Type> tipos;
    private List<Short> addresses;

    public IdentificationTable() {
        this.ids = new ArrayList<>();
        this.tipos = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public void enter(String id, String tipo, short address){
        Type type;
        switch (tipo) {
            case "integer" -> type = Type.type_integer;
            case "float" -> type = Type.type_float;
            case "boolean" -> type = Type.type_bool;
            default -> type = Type.type_error;
        }
        ids.add(id);
        tipos.add(type);
        addresses.add(address);
    }

    public Type retrieve(String id) {
        Type type = null;

        if(ids.contains(id)) {
            type = tipos.get(ids.indexOf(id));
        }

        return type;
    }

    public short getAddress(String id) {
        short returnAddr = -1;
        if(ids.contains(id)) {
            returnAddr = addresses.get(ids.indexOf(id));
        }

        return returnAddr;
    }
}
