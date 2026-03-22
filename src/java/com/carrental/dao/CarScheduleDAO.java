package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.CarSchedule;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarScheduleDAO {

    public boolean insertSchedule(CarSchedule schedule) {
        String sql = "INSERT INTO CarSchedules (CarID, ContractID, StartDateTime, EndDateTime, ScheduleStatus, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getCarID());
            ps.setObject(2, schedule.getContractID());
            ps.setTimestamp(3, schedule.getStartDateTime());
            ps.setTimestamp(4, schedule.getEndDateTime());
            ps.setString(5, schedule.getScheduleStatus());
            ps.setTimestamp(6, schedule.getCreatedAt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateScheduleStatusByContract(int contractID, String status) {
        String sql = "UPDATE CarSchedules SET ScheduleStatus = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private CarSchedule mapSchedule(ResultSet rs) throws Exception {
        CarSchedule schedule = new CarSchedule();
        schedule.setScheduleID(rs.getInt("ScheduleID"));
        schedule.setCarID(rs.getInt("CarID"));
        schedule.setContractID((Integer) rs.getObject("ContractID"));
        schedule.setStartDateTime(rs.getTimestamp("StartDateTime"));
        schedule.setEndDateTime(rs.getTimestamp("EndDateTime"));
        schedule.setScheduleStatus(rs.getString("ScheduleStatus"));
        schedule.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return schedule;
    }

    public List<CarSchedule> findActiveSchedulesByCar(int carID) {
        String sql = "SELECT * FROM CarSchedules "
                + "WHERE CarID = ? "
                + "AND ScheduleStatus IN ('Booked','InUse','Maintenance') "
                + "AND EndDateTime >= GETDATE() "
                + "ORDER BY StartDateTime";

        List<CarSchedule> schedules = new ArrayList<>();
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapSchedule(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedules;
    }
}
