package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.ContractDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ContractDetailDAO {

    public boolean insertContractDetail(ContractDetail detail) {
        String sql = "INSERT INTO ContractDetails (ContractID, CarID, HasDriver, DriverID, RentPrice, DriverFee, DetailStatus, DriverStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, detail.getContractID());
            ps.setObject(2, detail.getCarID());
            ps.setObject(3, detail.getHasDriver());
            ps.setObject(4, detail.getDriverID());
            ps.setBigDecimal(5, detail.getRentPrice());
            ps.setBigDecimal(6, detail.getDriverFee());
            ps.setString(7, detail.getDetailStatus());
            ps.setString(8, detail.getDriverStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignDriver(int contractID, int driverID) {
        String sql = "UPDATE ContractDetails SET DriverID = ?, HasDriver = 1 WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setInt(2, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDetailStatus(int contractID, String detailStatus, String driverStatus) {
        String sql = "UPDATE ContractDetails SET DetailStatus = ?, DriverStatus = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, detailStatus);
            ps.setString(2, driverStatus);
            ps.setInt(3, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDetailStatusOnly(int contractID, String detailStatus) {
        String sql = "UPDATE ContractDetails SET DetailStatus = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, detailStatus);
            ps.setInt(2, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
