package utilidades;

public class Utilidades {

    public static final String TABLA_RESERVA="reserva";
    public static final String CAMPO_ID= "id";
    public static final String CAMPO_FECHA_INICIO="fecha_inicio";
    public static final String CAMPO_FECHA_FIN="fecha_fin";
    public static final String CAMPO_CIUDAD="ciudad";
    public static final String CAMPO_ESTADO_RESERVA= "estado_reserva";
    public static final String CAMPO_DESCUENTO="descuento";
    public static final String CAMPO_PRECIO_TOTAL="precio_total";
    public static final String CAMPO_DIAS_RESERVA="dias_reserva";
    public static final String CAMPO_USUARIO_RESERVA= "usuario_reserva";
    public static final String CAMPO_SERVICIO_ESPECIFICO= "servicio_especifico";

    public static final String CREAR_TABLA_RESERVA="CREATE TABLE "+TABLA_RESERVA+" ("+CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+CAMPO_FECHA_INICIO+" TEXT, "+CAMPO_FECHA_FIN+" TEXT, "+CAMPO_CIUDAD+" TEXT, "+CAMPO_ESTADO_RESERVA+" TEXT, "+CAMPO_DESCUENTO+" TEXT, "+CAMPO_PRECIO_TOTAL+" TEXT, "+CAMPO_DIAS_RESERVA+" TEXT, "+CAMPO_USUARIO_RESERVA+" TEXT, "+CAMPO_SERVICIO_ESPECIFICO+" TEXT)";

    public static final String TABLA_REG_MASCOTA = "reg_mascota";
    public static final String CAMPO_ID_MASC= "id_masc";
    public static final String CAMPO_NOMBRE= "nombre_mascota";
    public static final String CAMPO_ANIOS= "anios_masc";
    public static final String CAMPO_MESES= "meses_masc";
    public static final String CAMPO_RAZA= "raza_masc";
    public static final String CAMPO_GENERO= "genero";
    public static final String CAMPO_TAMANNO= "tamanno_mascota";
    public static final String CAMPO_ACTIVO_MASCOTA= "activo_mascota";
    public static final String CAMPO_USUARIO_MASCOTA= "usuario_mascota";
    public static final String CAMPO_PRECIO_BANNO="precio_banno";
    public static final String CAMPO_PRECIO_PELUQUERIA="precio_peluqueria";

    public static final String CREAR_TABLA_REG_MASCOTA ="CREATE TABLE "+TABLA_REG_MASCOTA+" ("+CAMPO_ID_MASC+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_NOMBRE+" TEXT, "+CAMPO_ANIOS+" TEXT, "+CAMPO_MESES+" TEXT, "+CAMPO_RAZA+" TEXT, "+CAMPO_GENERO+" TEXT, "+CAMPO_TAMANNO+" TEXT, "+CAMPO_ACTIVO_MASCOTA+" TEXT, "+CAMPO_USUARIO_MASCOTA+" TEXT, "+CAMPO_PRECIO_BANNO+" TEXT, "+CAMPO_PRECIO_PELUQUERIA+" TEXT)";

    public static final String TABLA_PROSERVICIOS = "proservicios";
    public static final String CAMPO_ID_SERVICIO = "id_servicio";
    public static final String CAMPO_NOMBRE_SERVICIO = "nombre_servicio";
    public static final String CAMPO_DESCRIPCION_SERVICIO = "descripcion";
    public static final String CAMPO_VALOR = "valor";

    public static final String CREAR_TABLA_PROSERVICIOS ="CREATE TABLE "+TABLA_PROSERVICIOS+" ("+CAMPO_ID_SERVICIO+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_NOMBRE_SERVICIO+" TEXT, "+CAMPO_DESCRIPCION_SERVICIO+" TEXT, "+CAMPO_VALOR+" DOUBLE)";

    public static final String TABLA_DESCUENTOS = "descuentos";
    public static final String CAMPO_NOMBRE_DESCUENTO="nombre_descuento";
    public static final String CAMPO_ID_DESCUENTO = "id_descuentos";
    public static final String CAMPO_DIAS_INICIO = "dias_inicio";
    public static final String CAMPO_DIAS_FIN = "dias_fin";
    public static final String CAMPO_VALOR_DESCUENTO = "valor_descuento";
    public static final String CAMPO_ID_SERVICIO2 = "id_servicio2";

    public static final String CREAR_TABLA_DESCUENTOS ="CREATE TABLE "+TABLA_DESCUENTOS+" ("+CAMPO_ID_DESCUENTO+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+CAMPO_NOMBRE_DESCUENTO+" TEXT, "+CAMPO_DIAS_INICIO+" INTEGER, "+CAMPO_DIAS_FIN+" INTEGER,"+CAMPO_VALOR_DESCUENTO+" DOUBLE,"+CAMPO_ID_SERVICIO2+" INTEGER)";

    public static final String TABLA_USUARIO= "usuario";
    public static final String CAMPO_ID_USUARIO= "id_usuario";
    public static final String CAMPO_NOMBRE_USUARIO ="nombre_usuario";
    public static final String CAMPO_DOCUMENTO_USUARIO= "documento_usuario";
    public static final String CAMPO_CELULAR_USUARIO= "celular_usuario";
    public static final String CAMPO_MAIL_USUARIO= "mail_usuario";
    public static final String CAMPO_TIPO_USUARIO= "tipo_usuario";
    public static final String CAMPO_TIPO_DOCUMENTO= "tipo_documento";

    public static final String CREAR_TABLA_USUARIO ="CREATE TABLE "+TABLA_USUARIO+" ("+CAMPO_ID_USUARIO+" INTEGER PRIMARY KEY NOT NULL, "+CAMPO_NOMBRE_USUARIO+" TEXT, "+CAMPO_DOCUMENTO_USUARIO+" TEXT, "+CAMPO_CELULAR_USUARIO+" TEXT, "+CAMPO_MAIL_USUARIO+" TEXT, "+CAMPO_TIPO_USUARIO+" TEXT, "+CAMPO_TIPO_DOCUMENTO+" TEXT)";

    public static final String TABLA_DIRECCIONES= "mis_direcciones";
    public static final String CAMPO_ID_DIRECCIONES="id_direccion";
    public static final String CAMPO_CIUDAD_DIRECCION="ciudad";
    public static final String CAMPO_DIRECCION="direccion";
    public static final String CAMPO_DESC_CASA="descripcion_casa";
    public static final String CAMPO_SELECCION_DEFECTO="seleccion_defecto";
    public static final String CAMPO_ID_USUARIO_DIRECCION="usuario_direccion";

    public static final String CREAR_TABLA_DIRECCIONES ="CREATE TABLE "+TABLA_DIRECCIONES+" ("+CAMPO_ID_DIRECCIONES+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_CIUDAD_DIRECCION+" TEXT, "+CAMPO_DIRECCION+" TEXT, "+CAMPO_DESC_CASA+" TEXT, "+CAMPO_SELECCION_DEFECTO+" TEXT, "+CAMPO_ID_USUARIO_DIRECCION+" TEXT)";

    public static final String TABLA_MASCOTAS_RESERVADAS="mascotas_reservadas";
    public static final String CAMPO_ID_MASCOTARES="id_mascotaRes";
    public static final String CAMPO_ID_RESERVACION="id_reservacion";
    public static final String CAMPO_ID_MASCOTA="id_mascota";
    public static final String CAMPO_USUARIO_MASCOTA_RES= "usuario_mascota_res";

    public static final String CREAR_TABLA_MASCOTAS_RESERVADAS="CREATE TABLE "+TABLA_MASCOTAS_RESERVADAS+" ("+CAMPO_ID_MASCOTARES+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_ID_RESERVACION+"  TEXT, "+CAMPO_ID_MASCOTA+" TEXT, "+CAMPO_USUARIO_MASCOTA_RES+" TEXT)";

    public static final String TABLA_PRODUCTOS_TIENDA = "productos_tienda";
    public static final String CAMPO_ID_PRODUCTO = "id_producto";
    public static final String CAMPO_PRECIO_PROVEEDOR = "precio_proveedor";
    public static final String CAMPO_PRECIO_VENTA = "precio_venta";
    public static final String CAMPO_PRECIO_DESCUENTO = "precio_descuento";
    public static final String CAMPO_NOMBRE_PRODUCTO = "nombre_producto";
    public static final String CAMPO_CATEGORIA = "categoriaProductos";
    public static final String CAMPO_DESCRIPCION_PRODUCTO = "descripcion_producto";
    public static final String CAMPO_ESTRELLAS = "estrellas";
    public static final String CAMPO_CODIGO_BARRAS = "codigoBarras";
    public static final String CAMPO_STOCK = "stock";

    public static final String CREAR_TABLA_PRODUCTOS_TIENDA ="CREATE TABLE "+TABLA_PRODUCTOS_TIENDA+" ("+CAMPO_ID_PRODUCTO+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_PRECIO_PROVEEDOR+" TEXT, "+CAMPO_PRECIO_VENTA+" TEXT, "+CAMPO_PRECIO_DESCUENTO+" TEXT, "+CAMPO_NOMBRE_PRODUCTO+" TEXT, "+CAMPO_CATEGORIA+" TEXT, "+CAMPO_DESCRIPCION_PRODUCTO+" TEXT, "+CAMPO_ESTRELLAS+" TEXT, "+CAMPO_CODIGO_BARRAS+" TEXT,  "+CAMPO_STOCK+" TEXT)";

    public static final String TABLA_MULTIMEDIA_PRODUCTOS = "multimedia_productos";
    public static final String CAMPO_ID_MULTIM = "id_multim";
    public static final String CAMPO_NOMBRE_MULTIMEDIA = "nombre_multimedia";
    public static final String CAMPO_RUTA_MULTIMEDIA = "ruta_multimedia";
    public static final String CAMPO_ID_PRODUCTO_M = "id_producto_m";
    public static final String CAMPO_TIPO_MULTIMEDIA = "tipo_multimedia";

    public static final String CREAR_TABLA_MULTIMEDIA_PRODUCTOS="CREATE TABLE "+TABLA_MULTIMEDIA_PRODUCTOS+"("+CAMPO_ID_MULTIM+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_NOMBRE_MULTIMEDIA+" TEXT, "+CAMPO_RUTA_MULTIMEDIA+" TEXT, "+CAMPO_ID_PRODUCTO_M+" TEXT, "+CAMPO_TIPO_MULTIMEDIA+" TEXT )";

    public static final String TABLA_ETIQUETAS_PRODUCTOS = "etiqueta_productos";
    public static final String CAMPO_ID_ETIQUETA = "id_etiqueta";
    public static final String CAMPO_NOMBRE_ETIQUETA = "nombre_etiqueta";
    public static final String CAMPO_VALOR_ETIQUETA = "valor_etiqueta";
    public static final String CAMPO_ID_PRODUCTO_ETIQUETA = "id_producto_etiqueta";

    public static final String CREAR_TABLA_ETIQUETA_PRODUCTOS="CREATE TABLE "+TABLA_ETIQUETAS_PRODUCTOS+"("+CAMPO_ID_ETIQUETA+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_NOMBRE_ETIQUETA+" TEXT, "+CAMPO_VALOR_ETIQUETA+" TEXT, "+CAMPO_ID_PRODUCTO_ETIQUETA+" TEXT)";

    public static final String TABLA_VENTAS_TIENDA="ventas_tienda";
    public static final String CAMPO_ID_VENTA = "id_venta";
    public static final String CAMPO_FECHA_VENTA = "fecha_venta";
    public static final String CAMPO_PRECIO_VENTA_TIENDA = "precio_venta_tienda";
    public static final String CAMPO_DESCUENTO_VENTA = "descuento_venta";
    public static final String CAMPO_PRODUCTO = "producto";
    public static final String CAMPO_ID_CLIENTE = "id_cliente";
    public static final String CAMPO_DIRECCION_VENTA = "direccion_venta";
    public static final String CAMPO_CANTIDAD_PRODUCTO="cantidad_producto";
    public static final String CAMPO_ESTADO_COMPRA="estado_compra";

    public static final String CREAR_TABLA_VENTAS_TIENDA="CREATE TABLE "+TABLA_VENTAS_TIENDA+"("+CAMPO_ID_VENTA+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_FECHA_VENTA+" TEXT, "+CAMPO_PRECIO_VENTA_TIENDA+" TEXT, "+CAMPO_DESCUENTO_VENTA+" TEXT, "+CAMPO_PRODUCTO+" TEXT, "+CAMPO_ID_CLIENTE+" TEXT, "+CAMPO_DIRECCION_VENTA+" TEXT, "+CAMPO_CANTIDAD_PRODUCTO+" TEXT, "+CAMPO_ESTADO_COMPRA+" TEXT )";

    public static final String TABLA_RAZAS="precios_raza";
    public static final String CAMPO_ID_RAZA = "id_raza";
    public static final String CAMPO_RAZA_SERVER = "raza_server";
    public static final String CAMPO_BANNO_SERVER = "banno_server";
    public static final String CAMPO_PELUQUERIA_SERVER = "peluqueria_server";


    public static final String CREAR_TABLA_RAZAS="CREATE TABLE "+TABLA_RAZAS+"("+CAMPO_ID_RAZA+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_RAZA_SERVER+" TEXT, "+CAMPO_BANNO_SERVER+" TEXT, "+CAMPO_PELUQUERIA_SERVER+" TEXT)";

    public static final String TABLA_CUENTA="cuenta";
    public static final String CAMPO_ID_CUENTA = "id_cuenta";
    public static final String CAMPO_MAIL_CUENTA = "mail_cuenta";

    public static final String CREAR_TABLA_CUENTAS="CREATE TABLE "+TABLA_CUENTA+"("+CAMPO_ID_CUENTA+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+CAMPO_MAIL_CUENTA+" TEXT)";

}
