package services.interfaces;

import models.BloodRequest;
import java.util.List;
import java.util.Map;

public interface IBloodRequestService {
    void createRequest(BloodRequest request);
    void updateRequest(BloodRequest request);  // Add this method
    void updateRequestStatus(int requestId, String newStatus);
    void deleteRequest(int requestId);
    BloodRequest getRequestById(int requestId);
    List<BloodRequest> getAllRequests();
    List<BloodRequest> getRequestsByBloodType(String bloodType);
    List<BloodRequest> getRequestsByUrgency(String urgency);
    Map<String, Integer> getPendingRequestsByBloodType();
}