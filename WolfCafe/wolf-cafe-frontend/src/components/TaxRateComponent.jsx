import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentTaxRate, updateTaxRate } from '../services/TaxRateService';
import { isAdminUser } from '../services/AuthService';

const TaxRateComponent = () => {
  const [taxRate, setTaxRate] = useState(2.0);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAdminUser()) {
      navigate('/');
      return;
    }
    fetchTaxRate();
  }, [navigate]);

  const fetchTaxRate = async () => {
    try {
      setLoading(true);
      const response = await getCurrentTaxRate();
      console.log('Fetch response:', response.data);
      if (response.data && typeof response.data.rate === 'number') {
        setTaxRate(response.data.rate);
        setError('');
      } else {
        console.error('Invalid response format:', response);
        setError('Invalid response format from server');
      }
    } catch (err) {
      console.error('Error fetching tax rate:', err);
      setError(`Failed to fetch tax rate: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    const rateValue = parseFloat(taxRate);
    if (isNaN(rateValue) || rateValue < 0 || rateValue > 100) {
      setError('Please enter a number from 0-100');
      return;
    }

    try {
      setLoading(true);
      console.log('Sending update with rate:', rateValue);
      const response = await updateTaxRate(rateValue);
      console.log('Update response:', response.data);
      
      if (response.data && typeof response.data.rate === 'number') {
        setSuccess(<span style={{ color: 'green' }}>Tax rate was successfully updated!</span>);
        setTaxRate(response.data.rate);
      } else {
        throw new Error('Invalid response format from server');
      }
    } catch (err) {
      console.error('Error updating tax rate:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update tax rate';
      setError(<span style={{ color: 'red' }}>Error: {errorMessage}</span>);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setError('');
    const value = e.target.value;
    if (value === '' || !isNaN(value)) {
      setTaxRate(value);
    }
  };

  const handleIncrement = () => {
    setError('');
    setTaxRate(prev => {
      const newValue = parseFloat(prev) + 1;
      return newValue > 100 ? 100 : newValue;
    });
  };

  const handleDecrement = () => {
    setError('');
    setTaxRate(prev => {
      const newValue = parseFloat(prev) - 1;
      return newValue < 0 ? 0 : newValue;
    });
  };

  return (
    <div className="container mx-auto mt-8 max-w-md p-6">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold mb-6">Edit Tax Rate</h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="taxRate" className="block text-sm font-medium mb-2">
              Tax Rate (%)
            </label>
            <div className="flex items-center space-x-2">
              <button 
                type="button" 
                onClick={handleDecrement}
                disabled={loading || taxRate <= 0}
                className="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
              >
                -
              </button>
              <input
                id="taxRate"
                type="number"
                step="0.1"
                min="0"
                max="100"
                value={taxRate}
                onChange={handleInputChange}
                placeholder="Enter tax rate"
                disabled={loading}
                className="w-full px-3 py-2 border rounded text-center"
              />
              <button 
                type="button" 
                onClick={handleIncrement}
                disabled={loading || taxRate >= 100}
                className="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
              >
                +
              </button>
            </div>
          </div>

          {error && (
            <div className="bg-red-50 text-red-700 p-3 rounded border border-red-200 mt-4">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-50 text-green-700 p-3 rounded border border-green-200 mt-4">
              {success}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-500 text-black py-2 px-4 rounded hover:bg-blue-600 disabled:opacity-50 mt-4"
          >
            {loading ? 'Saving...' : 'Save Rate'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default TaxRateComponent;