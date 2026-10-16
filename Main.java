/**
 * PROGRAMA PRINCIPAL
 * Crea un empleado de cada tipo y los trata como "Empleado" (polimorfismo).
 */
public class Main {
    public static void main(String[] args) {

        // Arreglo de la superclase que guarda objetos de las 4 subclases
        Empleado[] empleados = {
            new EmpleadoAsalariado("Laura Pérez", "1001", 7, 3_000_000),
            new EmpleadoPorHoras("Carlos Gómez", "1002", 2, 50, 20_000),
            new EmpleadoPorComision("Ana Torres", "1003", 3, 1_500_000, 2_500_000, 0.10),
            new EmpleadoTemporal("Juan Díaz", "1004", 0, 1_800_000, 6)
        };

        System.out.println("===== NÓMINA DE EMPLEADOS =====");
        for (Empleado e : empleados) {
            e.mostrarResumen(); // Polimorfismo: cada objeto calcula a su manera
        }

        System.out.println("-----------------------------------------");
        System.out.println("Total de empleados creados: " + Empleado.getTotalEmpleados());
    }
}

/**
 * SUPERCLASE ABSTRACTA: Empleado
 * Define los atributos y métodos comunes a todos los tipos de empleado.
 * Es abstracta porque cada subclase calcula su salario de forma distinta.
 */
abstract class Empleado {

    // ---------- Constantes (deducciones obligatorias) ----------
    protected static final double PORC_SALUD = 0.04;    // 4% salud
    protected static final double PORC_PENSION = 0.04;  // 4% pensión

    // ---------- Atributo static: cuenta cuántos empleados se han creado ----------
    private static int totalEmpleados = 0;

    // ---------- Atributos comunes ----------
    protected String nombre;          // Nombre del empleado
    protected String identificacion;  // Número de identificación
    protected int aniosEmpresa;       // Años que lleva en la empresa
    protected double salarioBase;     // Salario base (mensual)

    /** Constructor: inicializa los atributos comunes. */
    public Empleado(String nombre, String identificacion, int aniosEmpresa, double salarioBase) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.aniosEmpresa = aniosEmpresa;
        this.salarioBase = salarioBase;
        totalEmpleados++;
    }

    /** Método abstracto: cada subclase define cómo calcula su salario bruto mensual. */
    public abstract double calcularSalarioBruto();

    /** Beneficios adicionales según el tipo de contrato. Por defecto no hay (0). */
    public double calcularBeneficios() {
        return 0;
    }

    /** Deducciones obligatorias: 4% salud + 4% pensión sobre el salario bruto. */
    public double calcularDeducciones() {
        return calcularSalarioBruto() * (PORC_SALUD + PORC_PENSION);
    }

    /** Salario neto = bruto + beneficios - deducciones. */
    public double calcularSalarioNeto() {
        return calcularSalarioBruto() + calcularBeneficios() - calcularDeducciones();
    }

    /** Devuelve el tipo de empleado (lo sobreescribe cada subclase). */
    public String getTipo() {
        return "Empleado";
    }

    /** Método static: devuelve cuántos empleados se han creado. */
    public static int getTotalEmpleados() {
        return totalEmpleados;
    }

    /** Muestra el resumen de pago del empleado. */
    public void mostrarResumen() {
        System.out.println("-----------------------------------------");
        System.out.println("Tipo:           " + getTipo());
        System.out.println("Nombre:         " + nombre);
        System.out.println("Identificación: " + identificacion);
        System.out.println("Años empresa:   " + aniosEmpresa);
        System.out.println("Salario bruto:  " + formato(calcularSalarioBruto()));
        System.out.println("Beneficios:     " + formato(calcularBeneficios()));
        System.out.println("Deducciones:    " + formato(calcularDeducciones()));
        System.out.println("SALARIO NETO:   " + formato(calcularSalarioNeto()));
    }

    /** Da formato de pesos a un valor numérico (ej: $1.500.000). */
    protected static String formato(double valor) {
        return String.format("$%,.0f", valor).replace(',', '.');
    }

    // ---------- Getters ----------
    public String getNombre() { return nombre; }
    public String getIdentificacion() { return identificacion; }
    public int getAniosEmpresa() { return aniosEmpresa; }
    public double getSalarioBase() { return salarioBase; }
}

/**
 * SUBCLASE: EmpleadoAsalariado
 * Recibe un salario mensual fijo. Si lleva más de 5 años en la empresa,
 * obtiene un bono anual del 10%.
 */
class EmpleadoAsalariado extends Empleado {

    private static final double PORC_BONO_ANUAL = 0.10; // 10% del salario anual

    public EmpleadoAsalariado(String nombre, String identificacion, int aniosEmpresa, double salarioMensual) {
        super(nombre, identificacion, aniosEmpresa, salarioMensual);
    }

    /** Salario bruto mensual: es fijo. */
    @Override
    public double calcularSalarioBruto() {
        return salarioBase;
    }

    /** Bono anual: 10% del salario anual si tiene más de 5 años en la empresa. */
    public double calcularBonoAnual() {
        if (aniosEmpresa > 5) {
            return salarioBase * 12 * PORC_BONO_ANUAL;
        }
        return 0;
    }

    /** Beneficio mensual: el bono anual repartido en 12 meses. */
    @Override
    public double calcularBeneficios() {
        return calcularBonoAnual() / 12;
    }

    @Override
    public String getTipo() {
        return "Empleado Asalariado";
    }
}

/**
 * SUBCLASE: EmpleadoPorHoras
 * Su salario depende de las horas trabajadas y la tarifa por hora.
 * Las horas adicionales a 40 se pagan con un incremento del 50%.
 */
class EmpleadoPorHoras extends Empleado {

    private static final int HORAS_NORMALES = 40;
    private static final double RECARGO_EXTRA = 1.5; // 50% adicional

    private double horasTrabajadas; // Horas trabajadas en el periodo
    private double tarifaPorHora;   // Valor de cada hora

    public EmpleadoPorHoras(String nombre, String identificacion, int aniosEmpresa,
                            double horasTrabajadas, double tarifaPorHora) {
        super(nombre, identificacion, aniosEmpresa, 0); // no tiene salario base fijo
        this.horasTrabajadas = horasTrabajadas;
        this.tarifaPorHora = tarifaPorHora;
    }

    /** Salario bruto: horas normales + horas extra con 50% de recargo. */
    @Override
    public double calcularSalarioBruto() {
        if (horasTrabajadas <= HORAS_NORMALES) {
            return horasTrabajadas * tarifaPorHora;
        }
        double horasExtra = horasTrabajadas - HORAS_NORMALES;
        return (HORAS_NORMALES * tarifaPorHora) + (horasExtra * tarifaPorHora * RECARGO_EXTRA);
    }

    @Override
    public String getTipo() {
        return "Empleado Por Horas";
    }
}

/**
 * SUBCLASE: EmpleadoPorComision
 * Recibe un sueldo base más un porcentaje de comisión sobre sus ventas.
 * Si supera los $2.000.000 en ventas, obtiene un bono del 5%.
 */
class EmpleadoPorComision extends Empleado {

    private static final double META_VENTAS = 2_000_000;
    private static final double PORC_BONO = 0.05; // 5% sobre las ventas

    private double ventas;            // Total vendido en el periodo
    private double porcentajeComision; // Ej: 0.10 = 10%

    public EmpleadoPorComision(String nombre, String identificacion, int aniosEmpresa,
                               double salarioBase, double ventas, double porcentajeComision) {
        super(nombre, identificacion, aniosEmpresa, salarioBase);
        this.ventas = ventas;
        this.porcentajeComision = porcentajeComision;
    }

    /** Salario bruto: sueldo base + comisión sobre ventas. */
    @Override
    public double calcularSalarioBruto() {
        return salarioBase + (ventas * porcentajeComision);
    }

    /** Beneficio: bono del 5% sobre las ventas si supera la meta. */
    @Override
    public double calcularBeneficios() {
        if (ventas > META_VENTAS) {
            return ventas * PORC_BONO;
        }
        return 0;
    }

    @Override
    public String getTipo() {
        return "Empleado Por Comisión";
    }
}

/**
 * SUBCLASE: EmpleadoTemporal
 * Tiene un contrato de duración limitada y no recibe bonos ni beneficios adicionales.
 */
class EmpleadoTemporal extends Empleado {

    private int duracionContratoMeses; // Duración del contrato en meses

    public EmpleadoTemporal(String nombre, String identificacion, int aniosEmpresa,
                            double salarioBase, int duracionContratoMeses) {
        super(nombre, identificacion, aniosEmpresa, salarioBase);
        this.duracionContratoMeses = duracionContratoMeses;
    }

    /** Salario bruto: solo el salario base, sin bonos. */
    @Override
    public double calcularSalarioBruto() {
        return salarioBase;
    }

    // No sobreescribe calcularBeneficios(): usa el de la superclase (0).

    @Override
    public String getTipo() {
        return "Empleado Temporal (" + duracionContratoMeses + " meses)";
    }
}
