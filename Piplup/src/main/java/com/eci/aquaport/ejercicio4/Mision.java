package com.eci.aquaport.ejercicio4;

public class Mision {
    private final String id;
    private final DroneAcuatico drone;
    private final String puntoPartida;
    private final String puntoLlegada;
    private final TipoCarga tipoCarga;
    private final EstadoMision estado;

    private Mision(Builder builder) {
        this.id = builder.id;
        this.drone = builder.drone;
        this.puntoPartida = builder.puntoPartida;
        this.puntoLlegada = builder.puntoLlegada;
        this.tipoCarga = builder.tipoCarga;
        this.estado = builder.estado;
    }

    public String getId() { return id; }
    public DroneAcuatico getDrone() { return drone; }
    public String getPuntoPartida() { return puntoPartida; }
    public String getPuntoLlegada() { return puntoLlegada; }
    public TipoCarga getTipoCarga() { return tipoCarga; }
    public EstadoMision getEstado() { return estado; }

    @Override
    public String toString() {
        return "Mision{" +
                "id='" + id + '\'' +
                ", drone=" + (drone != null ? drone.id() + " (" + drone.modelo() + ")" : "null") +
                ", puntoPartida='" + puntoPartida + '\'' +
                ", puntoLlegada='" + puntoLlegada + '\'' +
                ", tipoCarga=" + tipoCarga +
                ", estado=" + estado +
                '}';
    }

    public static class Builder {
        private String id;
        private DroneAcuatico drone;
        private String puntoPartida;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private EstadoMision estado = EstadoMision.PENDIENTE;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder drone(DroneAcuatico drone) {
            this.drone = drone;
            return this;
        }

        public Builder puntoPartida(String puntoPartida) {
            this.puntoPartida = puntoPartida;
            return this;
        }

        public Builder puntoLlegada(String puntoLlegada) {
            this.puntoLlegada = puntoLlegada;
            return this;
        }

        public Builder tipoCarga(TipoCarga tipoCarga) {
            this.tipoCarga = tipoCarga;
            return this;
        }

        public Builder estado(EstadoMision estado) {
            this.estado = estado;
            return this;
        }

        public Mision build() {
            if (id == null || id.isBlank()) {
                throw new IllegalStateException("El ID de la mision es obligatorio.");
            }
            if (drone == null) {
                throw new IllegalStateException("El drone es obligatorio.");
            }
            if (!drone.disponible()) {
                throw new IllegalStateException("El drone asignado (" + drone.id() + ") no esta disponible para la mision.");
            }
            if (puntoPartida == null || puntoPartida.isBlank()) {
                throw new IllegalStateException("El punto de partida es obligatorio y no puede estar vacio.");
            }
            if (puntoLlegada == null || puntoLlegada.isBlank()) {
                throw new IllegalStateException("El punto de llegada es obligatorio y no puede estar vacio.");
            }
            if (tipoCarga == null) {
                throw new IllegalStateException("El tipo de carga es obligatorio.");
            }
            return new Mision(this);
        }
    }
}