package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.Contract;
import com.carrental.entity.ContractReturnInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContractDAO {

    public int insertContract(Contract contract) {
        String sql = "INSERT INTO Contracts (ContractCode, CustomerID, StaffID, PickUpLocationID, DropOffLocationID, "
                + "StartDateTime, EndDateTime, TotalAmount, DepositAmount, CancelReason, CancelledBy, CreatedDate, Notes, StatusID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contract.getContractCode());
            ps.setObject(2, contract.getCustomerID());
            ps.setObject(3, contract.getStaffID());
            ps.setObject(4, contract.getPickUpLocationID());
            ps.setObject(5, contract.getDropOffLocationID());
            ps.setTimestamp(6, contract.getStartDateTime());
            ps.setTimestamp(7, contract.getEndDateTime());
            ps.setBigDecimal(8, contract.getTotalAmount());
            ps.setBigDecimal(9, contract.getDepositAmount());
            ps.setString(10, contract.getCancelReason());
            ps.setObject(11, contract.getCancelledBy());
            ps.setTimestamp(12, contract.getCreatedDate());
            ps.setString(13, contract.getNotes());
            ps.setObject(14, contract.getStatusID());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ContractReturnInfo getReturnInfo(int contractID) {
        String sql = "SELECT c.DropOffLocationID, cd.CarID FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "WHERE c.ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractReturnInfo info = new ContractReturnInfo();
                    info.setDropOffLocationID((Integer) rs.getObject("DropOffLocationID"));
                    info.setCarID(rs.getInt("CarID"));
                    return info;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public boolean updateReturnInfo(int contractID, Integer returnStaffID, String returnCondition) {
//        String sql = "UPDATE Contracts SET ReturnStaffID = ?, ReturnCondition = ? WHERE ContractID = ?";
//        try (Connection conn = new DBContext().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setObject(1, returnStaffID);
//            ps.setString(2, returnCondition);
//            ps.setInt(3, contractID);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    public boolean updateReturnInfo(int contractID, Integer returnStaffID, String returnCondition, Integer dropOffLocationID) {
        String sql = "UPDATE Contracts SET ReturnStaffID = ?, ReturnCondition = ?, DropOffLocationID = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, returnStaffID);
            ps.setString(2, returnCondition);
            ps.setObject(3, dropOffLocationID);
            ps.setInt(4, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateStartStaff(int contractID, Integer staffID) {
        String sql = "UPDATE Contracts SET StaffID = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, staffID);
            ps.setInt(2, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
