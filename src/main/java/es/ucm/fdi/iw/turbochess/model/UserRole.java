package es.ucm.fdi.iw.turbochess.model;

public enum UserRole {
    USER(0),			// non-privileged users (0 in DB)
    ADMIN(1);			// max-privileged users (1 in DB)
	
    private int role;
    private UserRole(int role){
        this.role = role;
    } 

    public static UserRole getRole(Integer roleInt) {
        if(roleInt== null) {
            return null;
        }

        for (UserRole role : UserRole.values()) {
            if (roleInt.equals(role.getRole())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Type " + roleInt + " of UserRole not found!");
    }

    public int getRole() {
        return role;
    }
}
