package repositories;

import dto.ProductoVentaDTO;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class ConsultaProduccionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall jdbcCall;

    @PostConstruct
    private void init() {
        this.jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("consultas")
            .withProcedureName("consultaProduccionPorAnio")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("p_anio", Types.INTEGER),
                new SqlOutParameter("p_resultado", OracleTypes.CURSOR, new ProductoVentaMapper())
            );
    }

    public List<ProductoVentaDTO> obtenerVentasPorAnio(int anio) {
        Map<String, Object> result = jdbcCall.execute(anio);
        return (List<ProductoVentaDTO>) result.get("p_resultado");
    }

    private static class ProductoVentaMapper implements RowMapper<ProductoVentaDTO> {
        @Override
        public ProductoVentaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductoVentaDTO productoVenta = new ProductoVentaDTO();
            productoVenta.setNombreProducto(rs.getString("nombre_producto"));
            productoVenta.setTotalVentas(rs.getDouble("total_ventas"));
            return productoVenta;
        }
    }
}
